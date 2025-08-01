package org.bezsahara.minikotlin.compiler.asm

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.method.*

class MetadataPass {
    private data class NameScope(val variableKey: VariableKey) {
        var startLabel: Label? = null

        val endCandidates = ArrayDeque<Label>()
    }

    private data class VariableKey(
        val index: Int,
        val typeInfo: TypeInfo,
        val name: String,
    )

    private val variableMap = hashMapOf<Int, NameScope>()

    private val variableInfo = arrayListOf<LocalVariableMetadata>()

    private fun addParameters(firstLabel: Label, parameters: List<KBMethod.Parameter>) {
        parameters.forEach { parameter ->
            variableMap[parameter.index] = NameScope(VariableKey(
                parameter.index,
                parameter.typeInfo,
                parameter.name
            )).also {
                it.startLabel
            }

        }
    }

    fun start(operations: List<KBByteCode>, parameters: List<KBMethod.Parameter>): List<KBByteCode> {

        val newOperations = ArrayList<KBByteCode>(operations.size)

        val firstIsLabelPoint = operations.first().actual().labelPointOrNull()
        val firstLabel = if (firstIsLabelPoint != null) {
            firstIsLabelPoint.label
        } else {
            val label = Label("TheStart")
            newOperations.add(LabelPoint(label))
            label
        }

        addParameters(firstLabel, parameters)

        for (operationOldIndex in operations.indices) {

            val operationOld = operations[operationOldIndex]
            try {
                val add = processOp(operationOld, newOperations, operationOldIndex, operations)
                if (add) {
                    newOperations.add(operationOld)
                }
            } catch (e: Throwable) {
                throw IllegalStateException("Failed at index $operationOldIndex", e)
            }
        }
        checkThatLabelsAreOk(newOperations)

        variableMap.values.forEach {
            val endLabel = it.endCandidates.last()

            val startLabel = it.startLabel ?: error("startLabel is null at $it")
            variableInfo.add(LocalVariableMetadata(
                it.variableKey.name,
                it.variableKey.typeInfo,
                startLabel,
                endLabel,
                it.variableKey.index
            ))
        }

        for (metadata in variableInfo) {
            newOperations.add(metadata)
        }

        checkThatLabelsAreOk(newOperations)
        return newOperations
    }

    private fun ArrayDeque<*>.tryRemoveLast() {
        if (isEmpty()) return
        removeLast()
    }

    private fun KBByteCode.labelPointOrNull(): LabelPoint? {
        return this as? LabelPoint
    }

    private fun checkThatLabelsAreOk(ops: List<KBByteCode>) {
        val map = hashMapOf<Label, LabelPoint>()
        var i = 0
//        println("--------------------------------------------")
        for (code in ops) {

            val actual = code.actual()

//            println("$i  $actual")
            if (actual is LabelPoint) {
                val mt = map[actual.label]

                if (mt != null) {
                    error("Should not happen at index $i")
                } else {
                    map[actual.label] = actual
                }
            }
            i++
        }
    }

    // Thanks to someone on stackoverflow I now know what to do
    // Currently works. But when examining bytecode it does not really work perfect.
    //                               I mean correct enough for decompiler to see names correctly.
    private fun processOp(operationOld: KBByteCode, newOperations: ArrayList<KBByteCode>, currentIndex: Int, allOps: List<KBByteCode>): Boolean {
        val operation = operationOld.actual()

        if (operation is NotFunctional) return true

        when (operation) {
            is KBIincOP -> TODO("When using iinc opcode, variable debug naming is not yet implemented for it. you can disable it in classproperties")

            // If it is a return we need to suggest to cancel the scope of the variable
            is KBSingleOP -> {

                if (operation.isReturn || operation.isThrow) {
                    val labelBefore = allOps.getOrNull(currentIndex-1)?.labelPointOrNull()
                    val labelAfter = allOps.getOrNull(currentIndex+1)?.labelPointOrNull()
                    appendNewExitLabel(newOperations, labelBefore ?: labelAfter)
                }

            }

            is KBVariableOP -> {
                if (operation.instruction.isStoreKind) {
                    val previous = variableMap[operation.variableIndex]

                    var wasHereBefore = false
                    if (previous != null) {
                        val previousVariableKey = previous.variableKey
                        if (previousVariableKey.name != operation.name || previousVariableKey.typeInfo != operation.descriptor) {
                            variableInfo.add(LocalVariableMetadata(
                                previousVariableKey.name,
                                previousVariableKey.typeInfo,
                                previous.startLabel!!, // create a start label if non was provided, can be used as the one before return or others
                                previous.endCandidates.last(),
                                previousVariableKey.index
                            ))
                        } else {
                            wasHereBefore = true
                        }
                    }

                    if (!wasHereBefore) {
                        operation.descriptor?.let {
                            if (operation.name != null) {
                                val variableKey = VariableKey(operation.variableIndex, it, operation.name)
                                val nameScope = NameScope(variableKey)
                                variableMap[operation.variableIndex] = nameScope

                                val labelAfter = allOps.getOrNull(currentIndex+1)?.labelPointOrNull()
                                if (labelAfter == null) {
                                    val label = Label("ST of ${operation.name}")
                                    newOperations.add(operationOld)
                                    newOperations.add(LabelPoint(label))
                                    nameScope.startLabel = label
                                    return false
                                } else {
                                    nameScope.startLabel = labelAfter.label
                                }
                            }
                        }
                    }
                    return true
                }

                if (operation.instruction.isLoadKind) {
                    val nameScope = variableMap[operation.variableIndex]

                    if (nameScope != null) {
                        if (nameScope.startLabel == null) {
                            val labelBefore = allOps.getOrNull(currentIndex-1)?.labelPointOrNull()?.label
                            if (labelBefore == null) {
                                val label = Label()
                                nameScope.startLabel = label
                                newOperations.add(LabelPoint(label))
                            } else {
                                nameScope.startLabel = labelBefore
                            }
                        }
                        nameScope.endCandidates.tryRemoveLast()
                    }
                }
            }

            is LabelPoint -> {
                variableMap.values.forEach {
                    it.endCandidates.addLast(operation.label)
                }
            }
            is KBByteCode.Debug -> error("")
            else -> Unit
        }

        return true
    }

    private fun appendNewExitLabel(newOperations: ArrayList<KBByteCode>, labelPointPresent: LabelPoint?) {
        if (labelPointPresent != null) {
            val label = labelPointPresent.label
            variableMap.values.forEach {
                it.endCandidates.addLast(label)
            }
        } else {
            val label = Label("Pos end")
            newOperations.add(LabelPoint(label))
            variableMap.values.forEach {
                it.endCandidates.addLast(label)
            }
        }
    }
}