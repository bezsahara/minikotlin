package org.bezsahara.minikotlin.lan.pieces

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.KVar
import org.bezsahara.minikotlin.lan.StackInfo
import kotlin.reflect.KClass

sealed interface ActionPiece



sealed interface SwitchPiece : ActionPiece {
    sealed interface Table : SwitchPiece
    sealed interface LookUpTable : SwitchPiece

    class TableStart(
        val numberInt: KRef.Native<Int>,
        val cases: IntArray,
    ) : SwitchPiece, Table

    class TableCaseStart(
        val onInt: Int,
        val tableStartId: Int,
    ) : SwitchPiece, Table

    class TableCaseDefaultStart(
        val tableStartId: Int,
    ) : SwitchPiece, Table

    class TableCaseEnd(
        val tableStartId: Int,
    ) : SwitchPiece, Table

    class TableEnd(
        val tableStartId: Int,
    ) : SwitchPiece, Table

    // Look up
    class LookUpTableStart(
        val numberInt: KRef.Native<Int>,
        val allCases: IntArray,
    ) : SwitchPiece, LookUpTable

    class LookUpTableCaseStart(
        val onInt: Int,
        val lookUpTableStartId: Int,
    ) : SwitchPiece, LookUpTable

    class LookUpTableCaseDefaultStart(
        val lookUpTableStartId: Int,
    ) : SwitchPiece, LookUpTable

    class LookUpTableCaseEnd(
        val lookUpTableStartId: Int,
    ) : SwitchPiece, LookUpTable

    class LookUpTableEnd(
        val lookUpTableStartId: Int,
    ) : SwitchPiece, LookUpTable
}


sealed interface WL : ActionPiece

data class WhileLoopPieceStart(
    val condition: KRef.Native<Boolean>,
    val whileFirst: Boolean,
) : WL

data class WhileLoopBreak(
    val startId: Int,
) : WL

data class WhileLoopPieceEnd(
    val startId: Int,
) : WL

data class CustomActionPiece(
    val kValue: KValue.ValueBlock,
) : ActionPiece


data class VariableSet(
    val varName: String,
    val variable: KVar<*>,
    val value: KRef<*>,
) : ActionPiece


enum class ConditionalOps {
    // Logical
//    And,
//    Or,
    Not,

    // Equality
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
    RefNotEqual
}

data class ConditionTransferPiece(
    val firstRef: KRef<*>,
    val secondRef: KRef<*>,
    val operation: ConditionalOps,
//    override val id: Int
) : KValue.ValueBlockReturns(arrayOf(firstRef, secondRef)) {
    private var labelJump: Label? = null

    fun needLabel(l: Label): Boolean {
        return when (operation) {
            ConditionalOps.Not -> {
                labelJump = l
                false
            }

            ConditionalOps.Equal,
            ConditionalOps.NotEqual,
            ConditionalOps.Greater,
            ConditionalOps.GreaterOrEqual,
            ConditionalOps.Less,
            ConditionalOps.LessOrEqual,
            ConditionalOps.IsNull,
            ConditionalOps.IsNotNull,
            ConditionalOps.RefEqual,
            ConditionalOps.RefNotEqual,
                -> {
                labelJump = l
                true
            }
        }
    }

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        when (operation) {
            ConditionalOps.Not -> {
                iconst_1()
                ixor()
            }

            ConditionalOps.Equal -> {
                stackInfo.pushArgument(0)
                stackInfo.pushArgument(1)
                jumpIfIntsEqual(labelJump!!)
            }

            ConditionalOps.NotEqual -> TODO()
            ConditionalOps.Greater -> TODO()
            ConditionalOps.GreaterOrEqual -> TODO()
            ConditionalOps.Less -> TODO()
            ConditionalOps.LessOrEqual -> TODO()
            ConditionalOps.IsNull -> TODO()
            ConditionalOps.IsNotNull -> TODO()
            ConditionalOps.RefEqual -> TODO()
            ConditionalOps.RefNotEqual -> TODO()
        }
    }

    override val objType: TypeInfo = TypeInfo.of<Boolean>()
}

abstract class RevealId() {
    private var idWasSet = false

    var id: Int = -1
        get() {
            if (idWasSet) {
                return field
            }
            error("id was not yet set!")
        }
        set(value) {
            if (idWasSet) error("Id was already set!")
            idWasSet = true
            field = value
        }
}

sealed interface CB : ActionPiece

// Is needed to ensure that user does not add random stuff in between cases
class ConditionStrictSpaceSTART(val casesIds: ArrayList<ConditionBlockStart>) : CB {
    var containsDefault = false

    override fun toString(): String {
        return "ConditionStrictSpaceSTART"
    }
}

data class ConditionBlockStart(
    val isCase: Boolean,
    val condition: KRef<Boolean>?,
    val condSStartId: Int,
    val markerAny: Any? = null
) : CB, RevealId() {
    init {
        require((isCase && condition != null) || !isCase && condition == null)
    }
}

class ConditionalBlockEnd(
    val startId: Int,
    val absoluteStartId: Int,
) : CB {
    override fun toString(): String {
        return "ConditionalBlockEnd"
    }
}

data class ConditionStrictSpaceEND(val condSStartId: Int) : CB

enum class MathOp {
    Add,
    Subtract,
    Multiply,
    Divide,
    Remainder,

//    Negate,  // unary -x

    // Bitwise (for integral types only)
    And,
    Or,
    Xor,
    ShiftLeft,
    ShiftRight,
    UnsignedShiftRight
}

fun sdf(a: Int) {
}

data class MathOperation(
    val firstRef: KRef.Native<*>,
    val secondRef: KRef.Native<*>,
    val operation: MathOp,
) : KValue.ValueBlockReturns(arrayOf(firstRef, secondRef)) {

    private var usingVariableInc = false

//    override val stackNeeded: Array<KRef<*>>?
//        get() {
//            val secondValue = secondRef.value
//            if (firstRef is KVar.Native && firstRef.kClass === Int::class && secondValue is KValue.Current<*>) {
//                val v = secondValue.v
//                if (v is Number && v !is Double && v !is Float && v.toInt() == -1 && v.toInt() == 1) {
//                    usingVariableInc = true
//                    return null
//                }
//            }
//            return arrayOf(firstRef, secondRef)
//        }

    init {
        require(firstRef.kClass == secondRef.kClass)
    }

    override val objType: TypeInfo = TypeInfo.Kt(firstRef.kClass)

    private fun KBMethod.emitMathOp(k: KClass<*>, op: MathOp) {
        when (k) {
            Int::class, Short::class, Byte::class, Char::class -> intOps(op)
            Long::class -> longOps(op)
            Float::class -> floatOps(op)
            Double::class -> doubleOps(op)
            else -> error("Unsupported primitive for math op: $k")
        }
    }

    private fun KBMethod.intOps(op: MathOp) = when (op) {
        MathOp.Add -> iadd()
        MathOp.Subtract -> isub()
        MathOp.Multiply -> imul()
        MathOp.Divide -> idiv()
        MathOp.Remainder -> irem()
        MathOp.And -> iand()
        MathOp.Or -> ior()
        MathOp.Xor -> ixor()
        MathOp.ShiftLeft -> ishl()
        MathOp.ShiftRight -> ishr()
        MathOp.UnsignedShiftRight -> iushr()
    }

    private fun KBMethod.longOps(op: MathOp) = when (op) {
        MathOp.Add -> ladd()
        MathOp.Subtract -> lsub()
        MathOp.Multiply -> lmul()
        MathOp.Divide -> ldiv()
        MathOp.Remainder -> lrem()
        MathOp.And -> land()
        MathOp.Or -> lor()
        MathOp.Xor -> lxor()
        MathOp.ShiftLeft -> lshl()
        MathOp.ShiftRight -> lshr()
        MathOp.UnsignedShiftRight -> lushr()
    }

    private fun KBMethod.floatOps(op: MathOp) = when (op) {
        MathOp.Add -> fadd()
        MathOp.Subtract -> fsub()
        MathOp.Multiply -> fmul()
        MathOp.Divide -> fdiv()
        MathOp.Remainder -> frem()
        else -> error("Bitwise/shift ops not valid for Float")
    }

    private fun KBMethod.doubleOps(op: MathOp) = when (op) {
        MathOp.Add -> dadd()
        MathOp.Subtract -> dsub()
        MathOp.Multiply -> dmul()
        MathOp.Divide -> ddiv()
        MathOp.Remainder -> drem()
        else -> error("Bitwise/shift ops not valid for Double")
    }

    override fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo) {
        if (usingVariableInc) {
            val i = variables[(firstRef as KVar.Native).name]!!
            iinc(
                i,
                secondRef.value.let { ((it as KValue.Current<*>).v as Number).toInt() }
            )
            iload(i)
            return
        }
        emitMathOp(firstRef.kClass, operation)
    }
}

/* ---------- helpers ---------- */


data class NumberConversion(
    val convertee: KRef.Native<*>,
    val toType: KClass<*>,
) : KValue.ValueBlockReturns(arrayOf(convertee)) {
    override val objType: TypeInfo = TypeInfo.Kt(toType)

    override fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo) {
        val to = toType
        when (convertee.kClass) {
            Int::class -> when (to) {
                Long::class -> i2l()
                Float::class -> i2f()
                Double::class -> i2d()
                Byte::class -> i2b()
                Char::class -> i2c()
                Short::class -> i2s()
            }

            Long::class -> when (to) {
                Int::class -> l2i()
                Float::class -> l2f()
                Double::class -> l2d()
            }

            Float::class -> when (to) {
                Int::class -> f2i()
                Long::class -> f2l()
                Double::class -> f2d()
            }

            Double::class -> when (to) {
                Int::class -> d2i()
                Long::class -> d2l()
                Float::class -> d2f()
            }
        }
    }
}