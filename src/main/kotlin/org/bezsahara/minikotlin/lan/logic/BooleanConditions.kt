package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.JumpInsnOp
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.method.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo
import java.util.*

interface NegationIsPossible {
    fun isNotInlinePossible(): Boolean
}

interface CanAcceptLabels {
    fun trySupplyLabels(successLabel: Label, failureLabel: Label): Boolean
}

fun KValue?.trySupplyLabels(successLabel: Label, failureLabel: Label): Boolean {
    return if (this is CanAcceptLabels) trySupplyLabels(successLabel, failureLabel) else false
}

enum class CondPieceType {
    And,
    Or
}

class CondPiece(val f: KRef<*>, tp: CondPieceType, val s: KRef<*>): KValue.ValueBlockReturns(), CanAcceptLabels, NegationIsPossible {
    var ended = false
    override val autoPush: Boolean = false
    private var canAddMore: Boolean = true

    var negation: Boolean = false

    override fun isNotInlinePossible(): Boolean {
        negation = true
        canAddMore = false
        return true
    }

    private val conditions = mutableListOf<Any>()

    init {
        conditions.add(f)
        conditions.add(tp)
        conditions.add(s)
    }

    var successLabel: Label? = null
    var failureLabel: Label? = null

    override fun trySupplyLabels(successLabel: Label, failureLabel: Label): Boolean {
        this.successLabel = successLabel
        this.failureLabel = failureLabel
        return true
    }

    fun join(type: CondPieceType, other: KRef.Native<Boolean>) {
        require(canAddMore)
        conditions.add(type)
        conditions.add(other)
    }

    sealed interface Cond {
        val operands: MutableList<Any>
        val t: CondPieceType
        data class And(override val operands: MutableList<Any>) : Cond {
            override val t: CondPieceType = CondPieceType.And
        }
        data class Or(override val operands: MutableList<Any>) : Cond {
            override val t: CondPieceType = CondPieceType.Or
        }
    }

    private val kRefStorage = IdentityHashMap<KRef<*>, Int>()
    private val tempKRefStorage = mutableListOf<KRef<*>>()

    private fun createBasedOnCPT(cpt: CondPieceType): Cond {
        return if (cpt == CondPieceType.Or) {
            Cond.Or(mutableListOf())
        } else {
            Cond.And(mutableListOf())
        }
    }

    fun process(): Cond {
        var currentCond: CondPieceType
        val baseCondition: Cond
        var cond: Cond = if (conditions.any { it == CondPieceType.Or }) {
            currentCond = CondPieceType.Or
            Cond.Or(mutableListOf())
        } else {
            currentCond = CondPieceType.And
            Cond.And(mutableListOf())
        }
        baseCondition = cond
        for (i in conditions.indices) {
            val value = conditions[i]
            val lookUp = conditions.getOrNull(i + 1)
            when (value) {
                is CondPieceType -> {
                    if (value == baseCondition.t) {
                        cond = baseCondition
                        currentCond = cond.t
                    }
                }
                is KRef<*> -> {
                    if (lookUp is CondPieceType) {
                        if (lookUp != currentCond && lookUp != baseCondition.t) {
                            val basedOnCPT = createBasedOnCPT(lookUp)
                            cond.operands.add(basedOnCPT)
                            cond = basedOnCPT
                            currentCond = cond.t
                        }

                    }
                    cond.operands.add(value)
                }
            }
        }
        return baseCondition
    }

    private fun StackInfo.pushRef(kRef: KRef<*>) {
        pushArgument(kRefStorage[kRef] ?: error("Could not find ref: $kRef"))
    }

    override val stackNeeded: Array<KRef<*>> by lazy {
        val tp = (conditions.filter { it is KRef<*> }.toList() as List<KRef<*>>)
        tp.forEachIndexed { index, ref ->
            kRefStorage[ref] = index
        }
        canAddMore = false
        tp.toTypedArray()
    }

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val processed = process()
//        println("Cond = ${prettyPrintCond(processed)}")

        val goodLabel = successLabel ?: Label()
        val badLabel = failureLabel ?: Label()

        goodLabel.name = "Nice Label"
        badLabel.name = "Bad Label"

        fun accessCond(cond: Cond, niceLabel: Label, worstLabel: Label) {
            when (cond) {
                is Cond.And -> {
//                    debugStackPrint("- Start of AND -")
                    cond.operands.forEachIndexed { index, any -> // true continue to next one, false - to badLabel
                        val first = index == 0
                        val last = index == cond.operands.lastIndex
                        val nextGoodLabel = if (last) {
                            niceLabel
                        } else {
                            Label("NextLabel for ${cond.operands[index+1]}")
                        }
                        when (any) {
                            is KRef<*> -> {
                                val supplyGood = if (negation) {
                                    if (last) worstLabel else nextGoodLabel
                                } else {
                                    nextGoodLabel
                                }

                                val supplyWorst = if (negation) {
                                    niceLabel
                                } else {
                                    worstLabel
                                }

                                val supplied = any.value.trySupplyLabels(supplyGood, supplyWorst)//(nextGoodLabel, worstLabel)
                                stackInfo.pushRef(any)
                                if (!supplied) {
                                    // means that either 1 or 0 was pushed
                                    if (!negation) {
                                        ifeq(worstLabel) // if false jump to worst
                                        goto(nextGoodLabel) // if true jump to next
                                    } else {
//                                        if (last) {
                                        ifeq(niceLabel) // if true
                                        if (!last) {
                                            goto(nextGoodLabel)
                                        } else {
                                            goto(worstLabel)
                                        }
//                                        } else {
//                                            ifeq(niceLabel) // if false jump to nice
//                                             otherwise go to next
//                                            goto(nextGoodLabel)
//                                        }
                                    }

                                }
                            }
                            is Cond -> accessCond(any, nextGoodLabel, worstLabel)
                        }
                        if (!last) {
                            labelPoint(nextGoodLabel)
                        }
                    }
//                    debugStackPrint("- END of AND -")
                }
                is Cond.Or -> {
//                    debugStackPrint("- Start of OR -")
                    var t= 0
                    cond.operands.forEachIndexed { index, any -> // true continue to next one, false - to badLabel
                        val first = index == 0
                        val last = index == cond.operands.lastIndex
                        val nextFailureLabel = if (last) {
                            worstLabel//niceLabel
                        } else {
                            t++
                            Label("OR next $t")
                        }
                        when (any) {
                            is KRef<*> -> {
                                val supplyGood = if (negation) {
                                    worstLabel
                                } else {
                                    niceLabel
                                }

                                val supplyBad = if (negation) {
                                    if (!last) {
                                        nextFailureLabel
                                    } else {
                                        niceLabel
                                    }
                                } else {
                                    nextFailureLabel
                                }

                                val supplied = any.value.trySupplyLabels(supplyGood, supplyBad)//(niceLabel, nextFailureLabel)

                                stackInfo.pushRef(any)
                                if (!supplied) {
                                    // means that either 1 or 0 was pushed
                                    if (!negation) {
                                        ifeq(nextFailureLabel) // if false then next
                                        goto(niceLabel)
                                    } else {
                                        if (!last) {
                                            ifeq(nextFailureLabel) // if false then next, which is good
                                        } else {
                                            ifeq(niceLabel) // if last is false then niceLabel
                                        }
                                        // otherwise true then worst
                                        goto(worstLabel)
                                    }

                                }
                            }
                            is Cond.And -> accessCond(any, niceLabel, nextFailureLabel)
                            else -> error("NOT yet supported")
                        }
                        if (!last) {
                            labelPoint(nextFailureLabel)
                        }
                    }
//                    debugStackPrint("- END of OR -")
                }
            }
        }


        val captured = capture {
            accessCond(processed, goodLabel, badLabel)
        }

        captured.update(reduceLabelNoise(captured.capturedOps))

        if (successLabel == null) {
            val finalLabel = Label()
            labelPoint(goodLabel)
            iconst_1()
            goto(finalLabel)

            labelPoint(badLabel)
            iconst_0()
            labelPoint(finalLabel)
        } else {
            // do nothing pretty much
        }
    }

    override val objType: TypeInfo = TypeInfo.Kt(Boolean::class)
}

@Deprecated("use actual", ReplaceWith("this.actual()"))
fun KBByteCode.unpack(): KBByteCode = if (this is KBByteCode.Debug) {
    this.value
} else {
    this
}


// Optimization that checks if all that jump does is:
//   Jumps to another jump command
//   It is always if some condition, then goto L OR label point then goto L
// TODO remake it
fun reduceLabelNoise(methods: MutableList<KBByteCode>): MutableList<KBByteCode> {

    /* ---------- 1st pass – strip redundant GOTOs ---------- */

    val pruned = mutableListOf<KBByteCode>()
    var i = 0
    while (i < methods.size) {
        val curPacked = methods[i]
        val cur  = curPacked.actual()
        val next = methods.getOrNull(i + 1)?.actual()

        // pattern:  GOTO Lx   followed immediately by   LabelPoint(Lx)
        val isRedundantGoto =
            cur  is KBJumpOP       &&
                    cur.instruction == JumpInsnOp.GOTO &&
                    next is LabelPoint     &&
                    cur.label === next.label           // identity match is fine; you used IdentityHashMap elsewhere

        if (isRedundantGoto) {
            /*  skip the GOTO – but keep the label point (next iteration). */
            i++           // advance past the GOTO
            continue
        }

        pruned += curPacked
        i++
    }

    /* ---------- 2nd pass – build exact reference counts ---------- */

    val refCount = IdentityHashMap<Label, Int>()

    fun inc(label: Label) {
        refCount[label] = (refCount[label] ?: 0) + 1
    }

    pruned.forEach { insnPacked ->
        val insn = insnPacked.actual()
        when (insn) {
            is LabelPoint -> refCount.putIfAbsent(insn.label, 0)

            is KBJumpOP -> inc(insn.label)

            is KBLookupSwitchOP -> {
                inc(insn.default)
                insn.cases.forEach(::inc)
            }

            is KBTableSwitchOP -> {
                inc(insn.default)
                insn.cases.forEach(::inc)
            }

            is KBTryCatchBlockOP -> {
                inc(insn.startTry)
                inc(insn.endTry)
                inc(insn.startCatch)
            }
            else -> Unit
        }
    }

    /* ---------- 3rd pass – drop unreferenced label points ---------- */

    return pruned
        .filterNot { it is LabelPoint && (refCount[it.label] ?: 0) == 0 }
        .toMutableList()
}




class kkkk(val k: String) : KRef.Native<Boolean>(Boolean::class, KValue.Current(k)) {
    override fun toString(): String {
        return "V($k)"
    }
}


fun q(a: String = "false"): KRef.Native<Boolean> {
    return kkkk(a)
}
fun prettyPrintCond(cond: CondPiece.Cond, indent: String = ""): String {
    val builder = StringBuilder()

    fun formatOperand(operand: Any, level: String) {
        when (operand) {
            is CondPiece.Cond -> builder.append(prettyPrintCond(operand, level))
            else -> builder.append("$level- $operand\n")
        }
    }

    val header = when (cond) {
        is CondPiece.Cond.And -> "${indent}And:\n"
        is CondPiece.Cond.Or -> "${indent}Or:\n"
    }
    builder.append(header)

    val newIndent = indent + "  "
    for (operand in cond.operands) {
        formatOperand(operand, newIndent)
    }

    return builder.toString()
}

fun main() {
    val cp = CondPiece(q("a"), CondPieceType.And, q("b"))
    cp.join(CondPieceType.Or, q("c"))
    cp.join(CondPieceType.And, q("d"))
    cp.join(CondPieceType.Or, q("e"))
    cp.join(CondPieceType.And, q("f"))
    cp.join(CondPieceType.And, q("g"))
    cp.join(CondPieceType.Or, q("h"))
    cp.join(CondPieceType.Or, q("i"))
    cp.join(CondPieceType.And, q("j"))
    cp.join(CondPieceType.And, q("k")) // add this if you want more depth

    val cond = cp.process()
    println(prettyPrintCond(cond))
}
