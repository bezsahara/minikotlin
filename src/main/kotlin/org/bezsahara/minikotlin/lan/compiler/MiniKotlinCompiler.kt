package org.bezsahara.minikotlin.lan.compiler

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.sameAs
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.method.KBTryCatchBlockOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.builder.opcodes.method.th
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.KVar
import org.bezsahara.minikotlin.lan.StackInfoImpl
import org.bezsahara.minikotlin.lan.helper.CodePiecesMap
import org.bezsahara.minikotlin.lan.logic.CanAcceptLabels
import org.bezsahara.minikotlin.lan.pieces.*
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap
import kotlin.reflect.KClass


fun tet(k: Any, c: Int) {
    when {
        "adfb" == k -> println("AS")
        "b" == k -> println("123")
        "c" == k -> println("43y")
        c > 8 -> println("ghdf")
        c % 5 == 0 -> println("34")
    }
}

class VariableManager private constructor(
    var highestIndexCounter: Int,
    val varIndexMap: MutableMap<String, Int>,
    var anonIndex: Long,
) {
    constructor(h: Int) : this(h, mutableMapOf(), 0)
    constructor() : this(0, mutableMapOf(), 0)

    fun createAChild(): VariableManager {
        // Changed to mut map
        return VariableManager(highestIndexCounter, varIndexMap.toMutableMap(), anonIndex)
    }

    fun createAnonName(): String {
        var possible: String
        do {
            possible = "avar$anonIndex"
            anonIndex += 1
        } while (varIndexMap.containsKey(possible))
        return possible
    }

    fun variableIndex(name: String, takeDouble: Boolean): Int {
        return varIndexMap.getOrPut(name) {
            val toR = highestIndexCounter
            highestIndexCounter += if (takeDouble) {
                2
            } else {
                1
            }
            toR
        }
    }

    fun variableIndexAuto(name: String, kClass: KClass<*>, asNative: Boolean = false): Int {
        if (asNative) {
            val takeDouble = when (kClass) {
                Double::class, Long::class -> true
                else -> false
            }
            return variableIndex(name, takeDouble)
        } else {
            return variableIndex(name, false)
        }
    }
}

class MiniKotlinCompiler(
    actionPiecesList: List<ActionPiece>,
    val variableMap: HashMap<String, KVar<*>>,
    val kbMethod: KBMethod,
) {
    val actionPieces: Array<ActionPiece> = CodePiecesMap(actionPiecesList).array
    private var highestIndexCounter = 0

    private val variableDeque = ArrayDeque<VariableManager>()

    init {
        val staticOffset = if (kbMethod.isStatic) 0 else 1

        val valuesSum = variableMap.values.filter { it.forcedIndex != -1 }.maxByOrNull { if (it.forcedIndex == -1) 0 else it.forcedIndex }
        val furtherOffset = if (valuesSum != null) {
            if (valuesSum is KVar.Native && (valuesSum.kClass == Double::class || valuesSum.kClass == Long::class)) {
                valuesSum.forcedIndex + 2
            } else {
                valuesSum.forcedIndex + 1
            }
        } else {
            staticOffset
        }

        variableDeque.addLast(
            VariableManager(furtherOffset)
        )
    }

    private fun initNewVariableManager(): VariableManager {
        val previousVM = variableDeque.last()
        val new = previousVM.createAChild()
        variableDeque.addLast(new)
        return new
    }

    private fun removeLastVariableManager() {
        variableDeque.removeLast()
        if (variableDeque.size == 0) {
            error("The variable deque is now empy. Which is wrong")
        }
    }

    val variableManager get() = variableDeque.last()

    private var currentCodePieceIndex = 0
    fun compile() {
        for (index in actionPieces.indices) {
            currentCodePieceIndex = index
            val codePiece = actionPieces[index]
            processCodePiece(codePiece)
        }
    }

    fun processCodePiece(actionPiece: ActionPiece) {
        when (actionPiece) {
            is CB -> processConditions(actionPiece)
            is CustomActionPiece -> processCustomAction(actionPiece)
            is VariableSet -> processVariable(actionPiece)
            is WL -> processLoop(actionPiece)
//            is CustomActionPieceReturns -> processCustomActionR(codePiece)
            is SwitchPiece.LookUpTable -> processLookupTable(actionPiece)
            is SwitchPiece.Table -> processSwitchPieceTable(actionPiece)
            is ThrowPiece -> processThrowPiece(actionPiece)
        }
    }

    data class ThrowPieceLabel(
        val startLabel: Label,
        val endLabel: Label,
        val startCatchStart: Label,
        val endCatchEnd: Label,
        val indexToInsert: Int
    ) {
        var errorType: Class<*>? = null
    }

    private val throwPieceMap = IntObjectHashMap<ThrowPieceLabel>()

    private fun processThrowPiece(throwPiece: ThrowPiece) {
        when (throwPiece) {
            is ThrowPiece.TryStart -> {
                initNewVariableManager()
                val indexToInsert = kbMethod.currentIndex + 1
                val labels = ThrowPieceLabel(
                    Label(), Label(), Label(), Label(), indexToInsert
                )
                kbMethod.labelPoint(labels.startLabel)
                throwPieceMap.put(currentCodePieceIndex, labels)
            }
            is ThrowPiece.TryEnd -> {
                removeLastVariableManager()
                val labels = throwPieceMap[throwPiece.tryStartId]!!
                kbMethod.labelPoint(labels.endLabel)
                kbMethod.goto(labels.endCatchEnd)
            }
            is ThrowPiece.CatchStart -> {
                initNewVariableManager()
                val labels = throwPieceMap[throwPiece.tryStartId]!!
                labels.errorType = throwPiece.errorType
                kbMethod.labelPoint(labels.startCatchStart)
                val catchVariable = throwPiece.catchVariable
                if (catchVariable == null) {
                    kbMethod.pop()
                } else {
                    kbMethod.astore(variableManager.variableIndex(catchVariable.name, false), catchVariable.name,
                        TypeInfo.Java(catchVariable.jClass))
//                    compileReference(kbMethod, catchVariable)
                }
            }
            is ThrowPiece.CatchEnd -> {
                removeLastVariableManager()
                val labels = throwPieceMap[throwPiece.tryStartId]!!
                kbMethod.labelPoint(labels.endCatchEnd)
                val errorType = labels.errorType
                if (errorType == null)
                    error("Error type was not set!")

                kbMethod.addOperationAtIndex(labels.indexToInsert, KBTryCatchBlockOP(
                    labels.startLabel,
                    labels.endLabel,
                    labels.startCatchStart,
                    TypeInfo.Java(errorType)
                ))
            }
        }
    }

    class LookUPTableLabels(
        val table: SwitchPiece.LookUpTableStart,
        val labelsMap: Array<Label>,
        val keysMap: IntArray,
        val defaultLabel: Label,
        val exit: Label,
    ) {
        fun getLabel(onInt: Int): Label {
            val index = keysMap.binarySearch(onInt)
            if (index < 0) error("Label was not found")
            return labelsMap[index]
        }
    }

    private val lookupMap = IntObjectHashMap<LookUPTableLabels>()

    private fun processLookupTable(table: SwitchPiece.LookUpTable) {
//        error("")
        when (table) {
            is SwitchPiece.LookUpTableStart -> {
                val lookupTableLabels = LookUPTableLabels(
                    table, Array(table.allCases.size) { Label() }, table.allCases, Label(), Label()
                )
                lookupMap.put(currentCodePieceIndex, lookupTableLabels)

                compileReference(kbMethod, table.numberInt)
                kbMethod.lookupSwitch(
                    lookupTableLabels.defaultLabel,
                    lookupTableLabels.keysMap,
                    lookupTableLabels.labelsMap
                )
            }

            is SwitchPiece.LookUpTableCaseStart -> {
                val lookupTableLabels = lookupMap[table.lookUpTableStartId]
                kbMethod.labelPoint(lookupTableLabels.getLabel(table.onInt))
                initNewVariableManager()
            }

            is SwitchPiece.LookUpTableCaseDefaultStart -> {
                val lookupTableLabels = lookupMap[table.lookUpTableStartId]
                kbMethod.labelPoint(lookupTableLabels.defaultLabel)
                initNewVariableManager()
            }

            is SwitchPiece.LookUpTableCaseEnd -> {
                val lookupTableLabels = lookupMap[table.lookUpTableStartId]
                kbMethod.goto(lookupTableLabels.exit)
                removeLastVariableManager()
            }

            is SwitchPiece.LookUpTableEnd -> {
                val lookupTableLabels = lookupMap[table.lookUpTableStartId]
                kbMethod.labelPoint(lookupTableLabels.exit)
            }
        }
    }

    class TableLabels(
        val table: SwitchPiece.TableStart,
        val labelsMap: Array<Label>,
        val defaultLabel: Label,
        val min: Int,
        val max: Int,
        val exit: Label,
    ) {
        fun getLabel(i: Int): Label {
            return labelsMap[i - min]
        }
    }

    private val tableMap = IntObjectHashMap<TableLabels>()

    private fun processSwitchPieceTable(tableBlock: SwitchPiece.Table) {
        when (tableBlock) {
            is SwitchPiece.TableStart -> {
                val tableLabels = tableBlock.cases.let {
                    TableLabels(tableBlock, Array(it.size) { Label() }, Label(), it.min(), it.max(), Label("TExit"))
                }

                tableMap.put(currentCodePieceIndex, tableLabels)
                compileReference(kbMethod, tableBlock.numberInt)
                kbMethod.tableSwitch(tableLabels.min, tableLabels.max, tableLabels.defaultLabel, tableLabels.labelsMap)
            }

            is SwitchPiece.TableCaseStart -> {
                val tableLabels = tableMap[tableBlock.tableStartId]
                kbMethod.labelPoint(tableLabels.getLabel(tableBlock.onInt))
                initNewVariableManager()
            }

            is SwitchPiece.TableCaseDefaultStart -> {
                initNewVariableManager()
                val tableLabels = tableMap[tableBlock.tableStartId]
                kbMethod.labelPoint(tableLabels.defaultLabel)
            }

            is SwitchPiece.TableCaseEnd -> {
                val tableLabels = tableMap[tableBlock.tableStartId]
                kbMethod.goto(tableLabels.exit)
                removeLastVariableManager()
            }

            is SwitchPiece.TableEnd -> {
                val tableLabels = tableMap[tableBlock.tableStartId]
                kbMethod.labelPoint(tableLabels.exit)
            }
        }
    }

    class WhileLabels(val startLabel: Label, val endLabel: Label, val wlps: WhileLoopPieceStart)

    private val whileLoopLabels = IntObjectHashMap<WhileLabels>()

    private fun processLoop(whileBlock: WL) {
        when (whileBlock) {
            is WhileLoopPieceStart -> {
                initNewVariableManager()
                val whileLabels = WhileLabels(Label("WhileStart"), Label("WhileEnd"), whileBlock)
                whileLoopLabels.put(currentCodePieceIndex, whileLabels)
                if (whileBlock.whileFirst) {
                    val nextLabel = Label()
                    kbMethod.labelPoint(whileLabels.startLabel)
                    val kValue = whileBlock.condition.value
                    if (kValue is CanAcceptLabels && kValue.trySupplyLabels(nextLabel, whileLabels.endLabel)) {
                        compileReference(kbMethod, whileBlock.condition)
                    } else {
                        compileReference(kbMethod, whileBlock.condition)
                        kbMethod.ifeq(whileLabels.endLabel)
                    }
                    kbMethod.labelPoint(nextLabel)
                } else {
                    kbMethod.labelPoint(whileLabels.startLabel)
                }
            }

            is WhileLoopBreak -> {
                kbMethod.goto(whileLoopLabels[whileBlock.startId].endLabel)
            }

            is WhileLoopPieceEnd -> {
                val whileLabels = whileLoopLabels[whileBlock.startId]
                if (whileLabels.wlps.whileFirst) {
                    kbMethod.goto(whileLabels.startLabel)
                    kbMethod.labelPoint(whileLabels.endLabel)
                } else {
                    val exitLabel = whileLabels.endLabel
                    val kValue = whileLabels.wlps.condition.value
                    if (kValue is CanAcceptLabels && kValue.trySupplyLabels(whileLabels.startLabel, exitLabel)) {
                        compileReference(kbMethod, whileLabels.wlps.condition)
                    } else {
                        compileReference(kbMethod, whileLabels.wlps.condition)
                        kbMethod.ifeq(exitLabel)
                        kbMethod.goto(whileLabels.startLabel)
                    }
                    kbMethod.labelPoint(exitLabel)
                }
                removeLastVariableManager()
                whileLoopLabels.remove(whileBlock.startId)
            }
        }
    }

    class CondLabelStore(
        val condId: Int,
        val label: Label,
    )

    class CondHS(
        val list: List<CondLabelStore>,
        val exit: Label,
    )

    private val conditionLabelMap = IntObjectHashMap<CondHS>()

    private fun processConditions(conditionBlock: CB) {
        when (conditionBlock) {
            is ConditionStrictSpaceSTART -> {
                val nm = mutableListOf<CondLabelStore>()
                conditionLabelMap.put(currentCodePieceIndex, CondHS(nm, Label()))
                conditionBlock.casesIds.forEach {
                    nm.add(CondLabelStore(it.id, Label()))
                }
//                nm.add(CondLabelStore(Int.MIN_VALUE, Label()))
            }

            is ConditionBlockStart -> {
                initNewVariableManager()
                val stores = conditionLabelMap[conditionBlock.condSStartId].list
                val labelI = stores.indexOfFirst { it.condId == conditionBlock.id }
                val bodyLabel = Label()
                val label = stores[labelI].label // current label
                // next label in line like else if
                val nextLabel = stores.getOrNull(labelI + 1).let {
                    it?.label ?: conditionLabelMap[conditionBlock.condSStartId].exit
                }

                nextLabel.name = "Next label"
                val kValue = conditionBlock.condition?.value
                kbMethod.labelPoint(label)
                if (kValue != null) {
                    if (kValue is CanAcceptLabels && kValue.trySupplyLabels(bodyLabel, nextLabel)) {
                        compileReference(kbMethod, conditionBlock.condition, Int.MIN_VALUE)
                    } else {
                        compileReference(kbMethod, conditionBlock.condition, Int.MIN_VALUE)
                        kbMethod.ifeq(nextLabel)
                    }
                    kbMethod.labelPoint(bodyLabel)
                }
            }

            is ConditionalBlockEnd -> {
                removeLastVariableManager()
                kbMethod.goto(conditionLabelMap[conditionBlock.absoluteStartId].exit)
            }

            is ConditionStrictSpaceEND -> {
                kbMethod.labelPoint(conditionLabelMap[conditionBlock.condSStartId].exit)
            }
        }
    }

    private fun processCustomAction(cap: CustomActionPiece) {
        writeValue(kbMethod, cap.kValue, Int.MIN_VALUE, null)
    }

    //data class VariableGet(
    //    val varName: String,
    //    val value: KRef<*>,
    //    override val id: Int
    //) : VariablePiece
    //data class VariableSet(
    //    val varName: String,
    //    val variable: KVar<*>,
    //    val value: KRef<*>,
    //    override val id: Int
    //) : VariablePiece {
    //    val variableIsField: Boolean get() = variable.value is KValue.FieldResult
    //}
    private fun processVariable(variablePiece: VariableSet) {
        val idx = variableManager.variableIndexAuto(
            variablePiece.variable.name,
            variablePiece.variable.kClass,
            variablePiece.variable is KVar.Native
        )
        if (compileReference(kbMethod, variablePiece.value, idx) == 0) {
            kbMethod.apply {
                if (variablePiece.variable is KVar.Native) {
                    storeAuto(idx, variablePiece.variable.kClass, variablePiece.varName, TypeInfo.Java(variablePiece.variable.jClass))
                } else {
                    astore(idx, variablePiece.varName, TypeInfo.Java(variablePiece.variable.jClass))
                }
            }
        }
    }

    // returns 0 for no effects
    // returns 10 for variable assign effect
    fun compileReference(kbm: KBMethod, r: KRef<*>, assignable: Int = Int.MIN_VALUE): Int {
        when (r) {
            is KVar -> {
                if (r.initialized) {
                    val asNative = r is KVar.Native
                    val idx = if (r.forcedIndex != -1) {
                        r.forcedIndex
                    } else {
                        variableManager.variableIndexAuto(r.name, r.kClass, asNative)
                    }
                    kbm.apply {
                        if (asNative) {
                            loadAuto(idx, r.kClass, r.name)
                        } else {
                            aload(idx, r.name)
                        }
                    }
                } else {
                    error("Variable $r is not initialized")
                }
            }

            is KRef.Native<*> -> {
                return writeValue(kbm, r.value, assignable, null)
            }

            is KRef.Obj<*> -> {
                return writeValue(kbm, r.value, assignable, r.jClass)
            }

            KRef.Nothing -> Unit // Just do nothing
        }
        return 0
    }

    private fun writeValue(kbm: KBMethod, value: KValue, assignable: Int, intendedValue: Class<*>?): Int {
        when (value) {
            is KValue.Current<*> -> {
                value.applyToKB(kbm)
            }

            KValue.NotPresent -> Unit

            // Here we just ensure that it was previous action and that stack is appended
//            is KValue.OpId -> {
//                require(currentCodePieceIndex - 1 == value.opId)
//            }

            is KValue.VB -> {
                val stackNeeded = value.stackNeeded
                val arr = if (stackNeeded != null) {
                    if (!value.autoPush) {
                        Array<KBMethod.() -> Unit>(stackNeeded.size) {
                            { compileReference(this, stackNeeded[it]) }
                        }
                    } else {
                        stackNeeded.forEach { compileReference(kbm, it) }
                        emptyArray()
                    }
                } else {
                    emptyArray()
                }

                val stackInfo = StackInfoImpl(Int.MIN_VALUE, variableManager, arr, value.autoPush, kbm)

                when (value) {
                    is KValue.ValueBlockAssignable -> TODO()
                    is KValue.ValueBlockReturns -> {
                        value.apply {
                            kbm.returns(variableManager.varIndexMap, stackInfo)
                        }
                    }

                    is KValue.ValueBlock -> {
                        value.apply {
                            kbm.init(variableManager.varIndexMap, stackInfo)
                        }
                    }
                }


                if (value is KValue.VBReturns) {

                    if (intendedValue != null) {
                        val typeInfoJava = TypeInfo.Java(intendedValue)

                        if ((value !is NoCastNeeded) && !(value.objType sameAs typeInfoJava) && !(value.objType sameAs TypeInfo.Void))
                            kbm.checkcast(typeInfoJava)
                    }
                }
            }

            is KValue.ValueBlockAssignable -> {
                TODO()
//                val arr = if (!value.autoPush) {
//                    Array<KBMethod.() -> Unit>(value.stackNeeded.size) {
//                        { compileReference(this, value.stackNeeded[it]) }
//                    }
//                } else {
//                    value.stackNeeded.forEach { compileReference(kbm, it) }
//                    emptyArray()
//                }
//                val stackInfo = StackInfoImpl(assignable, variableManager.highestIndexCounter, arr, value.autoPush, kbm)
//                value.apply {
//                    kbm.assignsOrReturns(variableManager.varIndexMap, stackInfo)
//                }
//                if (intendedValue != null && !stackInfo.usingAssignMode) {
//                    val typeInfoJava = TypeInfo.Java(intendedValue)
//                    if ((value !is NoCastNeeded) && !(value.objType sameAs typeInfoJava) && !(value.objType sameAs TypeInfo.Void))
//                        kbm.checkCast(typeInfoJava)
//                }
//                variableManager.highestIndexCounter = stackInfo.availableIndex
//                if (stackInfo.usingAssignMode) {
//                    return 10
//                }
            }
        }
        return 0
    }
}


object SR {
    const val Y = 3
}