package org.bezsahara.minikotlin.compiler

import org.bezsahara.minikotlin.builder.opcodes.codes.JumpInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.*
import java.util.*

//   $LabelPoint-L[3]
// 37  ILOAD 2 (a)
// 38  IFNE L(Nice Label)[30]
// 39  ILOAD 3 (b)
// 40  IFEQ L(OR next 2)[32]
// 41  ILOAD 4 (c)
// 42  IFNE L(Nice Label)[30]
// 43  ILOAD 5 (d)
// 44  IFEQ L(TExit)[11]
object Optimizations {
    fun useAll(operations: List<KBByteCode>): List<KBByteCode> {
//        if (false) {
//            return removeUnusedLabels(shortenLabelReach(removeUnusedLabels(operations)))
//        }
        return removeUnusedLabels(
                    removeRedirections(
                        removeUnusedLabels(
                            shortenLabelReach(
                                removeUnusedLabels(
                                    operations)))))
    }

    class LabelRedirection(
        val byteCodeOldOrigin: KBByteCode,
        val labelToReplace: Label,
        val toEqualsNull: Boolean
    )

    fun removeRedirections(operations: List<KBByteCode>): List<KBByteCode> {
        val newOperations = arrayListOf<KBByteCode>()

        val labelsRedirection = IdentityHashMap<KBByteCode, LabelRedirection>()


        for (operationOldIndex in operations.indices) {
            val operationOld = operations[operationOldIndex]
            val operation = operationOld.actual()

            if (operationOldIndex + 2 >= operations.lastIndex) continue


            //IFEQ = JumpInsnOp(153 ==0
            //IFNE = JumpInsnOp(154 !=0
            if (operation is KBJumpOP && operation.instruction.opcode in 153..154) { // find the pattern
                val next = operations[operationOldIndex + 1].actual()
                val afterNext = operations[operationOldIndex + 2].actual()
                if (next is KBJumpOP && next.instruction == JumpInsnOp.GOTO) {
                    if (afterNext is LabelPoint && afterNext.label === operation.label) {
                        val rd = LabelRedirection(
                            operationOld,
                            next.label,
                            operation.instruction.opcode != 153
                        )
                        labelsRedirection[operation] = rd
                    }
                }
            }
        }

        var skip = 0

        for (operationOldIndex in operations.indices) {
            val operationOld = operations[operationOldIndex]
            val operation = operationOld.actual()

            if (skip > 0) {
                skip--
                continue
            }

            val present = labelsRedirection[operation]
            if (present != null) {
                skip = 1
                val r = if (present.toEqualsNull) {
                    KBJumpOP(JumpInsnOp.IFEQ, present.labelToReplace)
                } else {
                    KBJumpOP(JumpInsnOp.IFNE, present.labelToReplace)
                }
                if (present.byteCodeOldOrigin is KBByteCode.Debug) {
                    newOperations.add(KBByteCode.Debug(r, present.byteCodeOldOrigin.debugInfo))
                } else {
                    newOperations.add((r))
                }
            } else {
                newOperations.add(operationOld)
            }
        }
        return newOperations
    }

    fun removeUnusedLabels(operations: List<KBByteCode>): List<KBByteCode> {
        val newOperations = arrayListOf<KBByteCode>()

        val labelsMap = IdentityHashMap<Label, Int>()

        fun Label.addToMap() {
            val present = labelsMap[this]
            if (present!=null) {
                labelsMap[this] = present+1
            } else {
                labelsMap[this] = 1
            }
        }

        operations.forEach { operationOld ->
            val operation = operationOld.actual()
            if (operation is LabelPresent) {
                when (operation) {
                    is KBJumpOP -> {
                        operation.label.addToMap()
                    }
                    is KBLookupSwitchOP -> {
                        operation.default.addToMap()
                        operation.cases.forEach { it.addToMap() }
                    }
                    is KBTableSwitchOP -> {
                        operation.default.addToMap()
                        operation.cases.forEach { it.addToMap() }
                    }
                    is KBTryCatchBlockOP -> {
                        operation.endTry.addToMap()
                        operation.startTry.addToMap()
                        operation.startCatch.addToMap()
                    }
                }
            }
        }

        for (operationOld in operations) {
            val operation = operationOld.actual()
            if (operation is LabelPoint) {
                if (labelsMap[operation.label] == null) {
                    continue // skip it
                }
            }
            newOperations.add(operationOld)
        }

        return newOperations
    }
    //   $LabelPoint-L(Bad Label)[53]
    //139  GOTO L(TExit)[11]

    class LabelReach(
        val labelOrigin: Label,
        val labelDirection: Label,
        val commandOriginOld: KBByteCode
    ) {
        var used = false
    }

    fun shortenLabelReach(operations: List<KBByteCode>): List<KBByteCode> {
        val newOperations = arrayListOf<KBByteCode>()

        val labelsReach = IdentityHashMap<Label, LabelReach>()


        for (operationOldIndex in operations.indices) {
            val operationOld = operations[operationOldIndex]
            val operation = operationOld.actual()

            if (operationOldIndex + 1 >= operations.lastIndex) continue

            if (operation is LabelPoint) { // find the pattern
                val next = operations[operationOldIndex + 1].actual()
                if (next is KBJumpOP && next.instruction == JumpInsnOp.GOTO) {
                    val lr = LabelReach(
                        operation.label,
                        next.label,
                        operationOld
                    )
                    labelsReach[operation.label] = lr
                }
            }
        }

        var skip = 0

        for (operationOldIndex in operations.indices) {
            val operationOld = operations[operationOldIndex]
            val operation = operationOld.actual()

            if (skip > 0) {
                skip--
                continue
            }

            if (operation is LabelPoint) {
                val reach = labelsReach[operation.label]
                if (reach != null) {
                    val actualNext = operations[operationOldIndex + 1].actual()
                    require(actualNext is KBJumpOP && actualNext.instruction == JumpInsnOp.GOTO)
                    skip = 1 // skip next goto
                    continue
                }
            }

            if (operation is LabelPresent) {
                when (operation) {
                    is KBJumpOP -> {
                        val reach = labelsReach[operation.label]
                        if (reach != null) {
                            reach.used = true
                            newOperations.add(KBJumpOP(operation.instruction, reach.labelDirection))
                            continue
                        }
                    }
                    is KBLookupSwitchOP -> {


//                        operation.default.addToMap()
//                        operation.cases.forEach { it.addToMap() }
                    }
                    is KBTableSwitchOP -> {
//                        operation.default.addToMap()
//                        operation.cases.forEach { it.addToMap() }
                    }
                    is KBTryCatchBlockOP -> {
//                        operation.endTry.addToMap()
//                        operation.startTry.addToMap()
//                        operation.startCatch.addToMap()
                    }
                }
            }
            newOperations.add(operationOld)
        }
        return newOperations
    }
}

