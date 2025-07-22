package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.lan.*

fun KRef.Native<Boolean>.end(): KRef.Native<Boolean> {
    this.value.let {
        if (it is CondPiece) {
            it.ended = true
            return this
        }
    }
    error("end() cannot be applied to $this")
}

operator fun KRef.Native<Boolean>.invoke(): KRef.Native<Boolean> {
    this.value.let {
        if (it is CondPiece) {
            it.ended = true
            return this
        }
    }
    error("end() (as invoke) cannot be applied to $this")
}

fun sys(a: Boolean, b: Boolean, c: Boolean, d: Boolean) {
    if (a xor b && c || d) {
        println("s")
    }
}

context(mk: MiniKotlin<*>)
infix fun KRef.Native<Boolean>.and(other: KRef.Native<Boolean>): KRef.Native<Boolean> {
    value.let {
        if (it is CondPiece && !it.ended) {
            it.join(CondPieceType.And, other)
            return this
        } else {
            val c = CondPiece(this, CondPieceType.And, other)
            return KRef.Native(Boolean::class, c)
        }
    }
}

context(mk: MiniKotlin<*>)
infix fun KRef.Native<Boolean>.or(other: KRef.Native<Boolean>): KRef.Native<Boolean> {
    value.let {
        if (it is CondPiece && !it.ended) {
            it.join(CondPieceType.Or, other)
            return this
        } else {
            val c = CondPiece(this, CondPieceType.Or, other)
            return KRef.Native(Boolean::class, c)
        }
    }
}

// Later NOT will be added

// Comparisons
enum class CompareType {
    Equal,
    NotEqual,

    // Relational
    Greater,
    GreaterOrEqual,
    Less,
    LessOrEqual,

    // Null checks
    IsNull,
    IsNotNull,

    // Reference identity
    RefEqual,
    RefNotEqual;

    fun opposite(): CompareType {
        return when (this) {
            Equal -> NotEqual
            NotEqual -> Equal
            Greater -> LessOrEqual
            GreaterOrEqual -> Less
            Less -> GreaterOrEqual
            LessOrEqual -> Greater
            IsNull -> IsNotNull
            IsNotNull -> IsNull
            RefEqual -> RefNotEqual
            RefNotEqual -> RefEqual
        }
    }
}

class CompareAndCheck(
    val firstRef: KRef<*>,
    val comparator: CompareType,
    secondRef: KRef<*>
) : KValue.ValueBlockReturns(arrayOf(firstRef, secondRef)), CanAcceptLabels, NegationIsPossible {
    override val objType: TypeInfo = TypeInfo.Kt(Boolean::class)

    private var successLabel: Label? = null
    private var failureLabel: Label? = null
    private var negation = false

    override fun isNotInlinePossible(): Boolean {
        negation = true
        return true
    }

    override fun trySupplyLabels(
        successLabel: Label,
        failureLabel: Label,
    ): Boolean {
        this.successLabel = successLabel
        this.failureLabel = failureLabel
        return true
    }

    private fun KBMethod.buildForFDL(niceLabel: Label, cmp: CompareType, tp: Int) {
        when (tp) {
            0 -> dcmpg()
            1 -> lcmp()
            2 -> fcmpg()
        }
        when (cmp) {
            CompareType.Equal -> ifeq(niceLabel)
            CompareType.NotEqual -> ifne(niceLabel)
            CompareType.Greater -> ifgt(niceLabel)
            CompareType.GreaterOrEqual -> ifge(niceLabel)
            CompareType.Less -> iflt(niceLabel)
            CompareType.LessOrEqual -> ifle(niceLabel)
            else -> error("Invalid comparison for fdl")
        }
    }

    private fun KBMethod.build(niceLabel: Label, cmp: CompareType) {
        val jClass = firstRef.jClass
        when {
            jClass === Double::class.java -> buildForFDL(niceLabel, cmp, 0)
            jClass === Long::class.java -> buildForFDL(niceLabel, cmp, 1)
            jClass === Float::class.java -> buildForFDL(niceLabel, cmp, 2)
            else -> when (cmp) {
                CompareType.Equal -> if_icmpeq(niceLabel) // jump if eq
                CompareType.NotEqual -> if_icmpne(niceLabel)
                CompareType.Greater -> if_icmpgt(niceLabel)
                CompareType.GreaterOrEqual -> if_icmpge(niceLabel)
                CompareType.Less -> if_icmplt(niceLabel)
                CompareType.LessOrEqual -> if_icmple(niceLabel)
                CompareType.IsNull -> ifnull(niceLabel)
                CompareType.IsNotNull -> ifnonnull(niceLabel)
                CompareType.RefEqual -> if_acmpeq(niceLabel)
                CompareType.RefNotEqual -> if_acmpne(niceLabel)
            }
        }
    }

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val niceLabel = successLabel ?: Label()

        if (negation) {
            build(niceLabel, comparator.opposite())
        } else {
            build(niceLabel, comparator)
        }

        if (successLabel == null) {
            val finalLabel = Label()
            iconst_0()
            goto(finalLabel)

            labelPoint(niceLabel)
            iconst_1()
            labelPoint(finalLabel)
        } else {
            goto(failureLabel!!)
        }
    }
}


infix fun <T: Number> KRef.Native<T>.greaterThan(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.Greater, other))
}

infix fun <T: Number> KRef.Native<T>.greaterOrEq(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.GreaterOrEqual, other))
}

infix fun <T: Number> KRef.Native<T>.lessThan(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.Less, other))
}

infix fun <T: Number> KRef.Native<T>.lessOrEq(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.LessOrEqual, other))
}

infix fun <T: Number> KRef.Native<T>.eq(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.Equal, other))
}

infix fun <T: Number> KRef.Native<T>.notEq(other: KRef.Native<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.NotEqual, other))
}

infix fun KRef.Obj<*>.eq(other: KRef.Obj<*>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.RefEqual, other))
}

infix fun <T: Number> KRef.Obj<T>.notEq(other: KRef.Obj<T>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.RefNotEqual, other))
}

fun <T: Any> KRef.Obj<T>.isNull(): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.IsNull, kRefNothing<Any>()))
}

fun <T: Any> KRef.Obj<T>.isNotNull(): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, CompareAndCheck(this, CompareType.IsNotNull, kRefNothing<Any>()))
}
