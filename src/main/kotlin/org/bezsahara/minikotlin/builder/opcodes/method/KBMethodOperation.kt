package org.bezsahara.minikotlin.builder.opcodes.method

//import org.bezsahara.kbytes.builder.declaration.getByteCodeMD
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.*
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Type

sealed interface LabelPresent {
}


data class KBSingleOP(
    val instruction: InsnOp
) : KBByteCode() {

    val isReturn: Boolean = instruction.opcode in 172..177
    val isThrow: Boolean = instruction.opcode == 191

    override fun getBytesSize(currentOffset: Int): Int {
        return 1
    }

    override fun justInsnName(): String {
        return instruction.name
    }

    override fun equals(other: Any?): Boolean {
        return if (other is KBSingleOP) {
            other.instruction.opcode == instruction.opcode
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return instruction.opcode.hashCode()
    }

    override fun toString(): String {
        return instruction.name
    }

    fun save(): String {
        return instruction.opcode.toString()
    }

    companion object {
        val prefix: Char = 'a'

        fun recover(saved: String): KBByteCode {
            return KBSingleOP(InsnOp.ALL_INSNS[saved.toInt()])
        }

        private val cache = IntObjectHashMap<KBSingleOP>()

        fun createOrGet(ins: InsnOp): KBSingleOP {
            var result = cache[ins.opcode]
            if (result == null) {
                result = KBSingleOP(ins)
                cache.put(ins.opcode, result)
                return result
            } else {
                return result
            }
        }
    }
}

data class KBSingleIntOP(
    val instruction: IntInsnOp,
    val operand: Int,
    val typeInfo: TypeInfo?
) : KBByteCode() {
    override fun getBytesSize(currentOffset: Int): Int {
        return instruction.size
    }

    override fun toString(): String {
        return "${instruction.asString} $operand"
    }

    override fun justInsnName(): String {
        return instruction.asString
    }

    fun save(): String {
        return "${instruction.opcode} $operand"
    }

    companion object {
        val prefix: Char = 'b'

//        fun recover(saved: String): KBSingleIntOP {
//
//        }
    }
}

class KBIincOP(
    val variableIndex: Int,
    val increment: Int
) : KBByteCode() {
    override fun getBytesSize(currentOffset: Int): Int {
        return if (variableIndex in 0..255 && increment in -128..127) {
            3  // Regular IINC: opcode + varIndex + const
        } else {
            6  // WIDE IINC: wide prefix + opcode + 2-byte varIndex + 2-byte const
        }
    }

    override fun toString(): String {
        return "IINC $variableIndex $increment"
    }

    override fun justInsnName(): String {
        return "IINC"
    }

    fun save(): String {
        return "$variableIndex $increment"
    }
}

data class LabelPoint(val label: Label) : KBByteCode() {
    override fun getBytesSize(currentOffset: Int): Int {
        return -1
    }
    override fun toString(): String {
        return "${'$'}LabelPoint-$label"
    }
    override fun justInsnName(): String {
        return label.toString()
    }
    fun save(): String {
        return label.itsID.toString()
    }
}

class KBVariableOP(
    val instruction: VarInsnOp,
    val variableIndex: Int,
    val name: String? = null,
    descriptor: TypeInfo? = null
) : KBByteCode() {
    val descriptorWasNull = descriptor == null
    var descriptor: TypeInfo? = descriptor ?: when (instruction.opcode) {
        21 -> TypeInfo.Int//SWord.I
        22 -> TypeInfo.Long//SWord.L
        23 -> TypeInfo.Float//SWord.F
        24 -> TypeInfo.Double//SWord.D
        25 -> TypeInfo.Object//SWord.A

        54 -> TypeInfo.Int //SWord.I
        55 -> TypeInfo.Long //SWord.L
        56 -> TypeInfo.Float //SWord.F
        57 -> TypeInfo.Double //SWord.D
        58 -> TypeInfo.Object //SWord.A

        else -> null
    }


    override fun getBytesSize(currentOffset: Int): Int {
        return when(variableIndex) {
            in 0..3 -> 1
            in 0..255 -> 2
            else -> 4
        }
    }

    override fun toString(): String {
        if (name == null) {
            return "${instruction.stringRep} $variableIndex"
        }
        return "${instruction.stringRep} $variableIndex ($name)"
    }
    override fun justInsnName(): String {
        return instruction.stringRep
    }
    fun save(): String {
        return "${instruction.opcode} $variableIndex"
    }
}

class KBMethodCallOP(
    val instruction: MethodInsnOp,
    val owner: TypeInfo,
    val name: String,
    val descriptor: MethodDescriptor,
    val isInterface: Boolean = false
) : KBByteCode() {
    override fun getBytesSize(currentOffset: Int): Int {
        return when (instruction.opcode) {
            185 -> 5
            else -> 3
        }
    }
    override fun toString(): String {
        return "${instruction.asString} ${owner.getStringRep()}.$name ${descriptor.getReturnSignature()}"
    }
    override fun justInsnName(): String {
        return instruction.asString
    }
    fun save(): String {
        return "${instruction.opcode} ${owner.getStringRep()}.$name ${descriptor.getReturnSignature()}"
    }
}

class KBFieldOP(
    val instruction: FieldInsnOp,
    val owner: TypeInfo,
    val name: String,
    val descriptor: TypeInfo
) : KBByteCode() {
    override fun getBytesSize(currentOffset: Int): Int {
        return 3
    }

    override fun justInsnName(): String {
        return instruction.asString
    }

    override fun toString(): String {
        return "${instruction.asString} ${owner.getStringRep()}.$name : ${descriptor.getReturnStringRep()}"
    }
}

enum class LdcType {
    String,
    Numbers,
    MethodType,
    MethodHandle,
    ConstantDynamic
}

class KBLdcOP private constructor(
    val value: Any,
    val type: LdcType,
    val ldcDebugInfo: Int
) : KBByteCode() {

    constructor(boolean: Boolean, ldcDebugInfo: Int) : this(boolean, LdcType.Numbers, ldcDebugInfo)
    constructor(char: Char, ldcDebugInfo: Int) : this(char, LdcType.Numbers, ldcDebugInfo)
    constructor(value: String, ldcDebugInfo: Int) : this(value, LdcType.String, ldcDebugInfo)
    constructor(methodType: TypeInfo, ldcDebugInfo: Int) : this(methodType, LdcType.MethodType, ldcDebugInfo)
    constructor(number: Number, ldcDebugInfo: Int) : this(number, LdcType.Numbers, ldcDebugInfo)

    fun getASMValue(): Any {
        return when (value) {
            is TypeInfo.JClassAvailable -> Type.getType(value.jClass)
            is TypeInfo.CustomType -> Type.getType(value.type)
            is Boolean -> if (value) 1 else 0
            else -> value
        }
    }

    override fun getBytesSize(currentOffset: Int): Int {
        return if (ldcDebugInfo > 255 || value is Long || value is Double) {
            3
        } else {
            2
        }
    }

    override fun justInsnName(): String {
        return "LDC"
    }

    override fun toString(): String {
        if (value is Number) {
            return "LDC $value"
        }
        if (value is TypeInfo) {
            return "LDC <${value.getReturnStringRep()}>"
        }
        return "LDC '$value'"
    }

    fun save(): String {
        return "${type.ordinal}$value"
    }
}

class KBTableSwitchOP(
    val min: Int,
    val max: Int,
    val default: Label,
    val cases: Array<out Label>
) : KBByteCode(), LabelPresent {
    init {
        require(max - min + 1 == cases.size)
        default.markAsControlFlowLabel()
        cases.forEach {
            it.markAsControlFlowLabel()
        }
    }

    override fun getBytesSize(currentOffset: Int): Int {
        val padding = (4 - ((currentOffset + 1) % 4)) % 4
        val jumpCount = max - min + 1
        return 1 + padding + 12 + (jumpCount * 4)
    }

    override fun justInsnName(): String {
        return "TABLESWITCH"
    }

    override fun toString(): String {
        return buildString {
            append("TABLESWITCH\n")
            cases.forEachIndexed { index, label ->
                append("    ")
                append(index + min)
                append(": ")
                append(label.toString())
                append('\n')
            }
            append("    default: ")
            append(default.toString())
        }
    }
}

class KBLookupSwitchOP(
    val default: Label,
    val keys: IntArray,
    val cases: Array<out Label>
) : KBByteCode(), LabelPresent {
    init {
        require(keys.size == cases.size) { "keys and cases must match in size" }
        default.markAsControlFlowLabel()
        cases.forEach {
            it.markAsControlFlowLabel()
        }
    }

    override fun getBytesSize(currentOffset: Int): Int {
        val padding = (4 - ((currentOffset + 1) % 4)) % 4
        val pairCount = keys.size
        return 1 + padding + 8 + pairCount * 8
        // 1 = opcode
        // padding = align to 4-byte
        // 8 = default (4) + npairs (4)
        // each pair = 4 (key) + 4 (offset)
    }

    override fun justInsnName(): String {
        return "LOOKUPSWITCH"
    }
    override fun toString(): String {
        return buildString {
            append("LOOKUPSWITCH")
            for (sharedIndex in 0..keys.size-1) {
                val label = cases[sharedIndex]
                append("  ")
                append(keys[sharedIndex])
                append(": ")
                append(label.toString())
                append('\n')
            }
            append("  default: ")
            append(default.toString())
        }
    }

    fun save(): String {
        return buildString {
            append(default.itsID)
            append('k')
            keys.forEach {
                append(it)
                append(',')
            }
            append('k')
            cases.forEach {
                append(it.itsID)
                append(',')
            }
        }
    }
}

class KBTypeOP(
    val instruction: TypeInsnOp,
    val typeInfo: TypeInfo
) : KBByteCode() {
    var s = Any()

    override fun getBytesSize(currentOffset: Int): Int {
        return 3
    }

    override fun justInsnName(): String {
        return instruction.asString
    }

    override fun toString(): String {
        return "${instruction.asString} ${typeInfo.getReturnStringRep()}"
    }
}

class KBJumpOP(
    val instruction: JumpInsnOp,
    val label: Label
) : KBByteCode(), LabelPresent {
    init {
        label.markAsControlFlowLabel()
    }

    override fun getBytesSize(currentOffset: Int): Int {
        TODO("cannot be implemented")
    }

    override fun toString(): String {
        return "${instruction.asString} $label"
    }
    override fun justInsnName(): String {
        return instruction.asString
    }
    fun save(): String {
        return "${instruction.opcode} ${label.itsID}"
    }
}

class KBTryCatchBlockOP(
    val startTry: Label,
    val endTry: Label,
    val startCatch: Label,
    val exceptionType: TypeInfo
) : KBByteCode(), LabelPresent {
    init {
//        startTry.markAsControlFlowLabel()
//        endTry.markAsControlFlowLabel()
        startTry.markAsCatchBlock()
        endTry.markAsCatchBlock()
        startCatch.markAsCatchBlock()
    }

    override fun toString(): String {
        return "TRYCATCHBLOCK TRY { $startTry | $endTry } CATCH: $startCatch "
    }

    override fun getBytesSize(currentOffset: Int): Int {
        TODO("Not yet implemented")
    }
    override fun justInsnName(): String {
        return "TryCatch"
    }
}

// Will be run only when ASM compiler is used (which is currently always unless you specify otherwise)
// Otherwise does nothing
// The only thing is that you do need to specify what stack it updates for verifier
// ClassWriter uses Compute frames so you do not need them, also do not call end or maxs.
// And do not refer to labels established within other parts of code or such blocks
abstract class KBAsmOp : KBByteCode() {
    abstract val canRecover: Boolean

    open val stackTakes: Array<SWord>?
        get() = emptyArray()

    open val stackGives: Array<SWord>?
        get() = emptyArray()

    abstract fun MethodVisitor.visit()
}