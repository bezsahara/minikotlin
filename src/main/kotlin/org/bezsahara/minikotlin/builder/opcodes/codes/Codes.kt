@file:Suppress("SpellCheckingInspection", "unused")

package org.bezsahara.minikotlin.builder.opcodes.codes

import org.bezsahara.minikotlin.builder.opcodes.codes.SWord.*

@JvmField val emptyArrayOfSWord = emptyArray<SWord>()

// TODO check for correct stack effects since it was written by chatpgt mostly. partially checked
// TODO ditribute in separate files

class IntInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: SWord?,
    @JvmField val stackGiven: SWord,
    val asString: String,
    @JvmField val size: Int
) {
    companion object {
        @JvmField val BIPUSH   = IntInsnOp(16, null, I.B,"BIPUSH" , 2)  // Opcodes.BIPUSH
        @JvmField val SIPUSH   = IntInsnOp(17, null, I.S,"SIPUSH" , 3)  // Opcodes.SIPUSH

        // Special case
        @JvmField val NEWARRAY = IntInsnOp(188, I, A, "NEWARRAY", 2) // Opcodes.NEWARRAY

        @JvmField val ALL = arrayOf(BIPUSH, SIPUSH, NEWARRAY)
    }

    override fun toString(): String {
        return asString
    }
}

// --------------------------------------------------------------------------------
// Value class for local‐variable instructions (visitVarInsn)
// --------------------------------------------------------------------------------
class VarInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: SWord?,
    @JvmField val stackGiven: SWord?,
    val stringRep: String
) {

    val isStoreKind = opcode in 54..58
    val isLoadKind = opcode in 21..25

    override fun toString(): String {
        return stringRep
    }

    companion object {
        @JvmField val ILOAD   = VarInsnOp(21, null, I, "ILOAD")
        @JvmField val LLOAD   = VarInsnOp(22, null, L, "LLOAD")
        @JvmField val FLOAD   = VarInsnOp(23, null, F, "FLOAD") // float as 32-bit
        @JvmField val DLOAD   = VarInsnOp(24, null, D, "DLOAD")
        @JvmField val ALOAD   = VarInsnOp(25, null, A, "ALOAD")

        @JvmField val ISTORE  = VarInsnOp(54, I, null, "ISTORE")
        @JvmField val LSTORE  = VarInsnOp(55, L, null, "LSTORE")
        @JvmField val FSTORE  = VarInsnOp(56, F, null, "FSTORE") // float as 32-bit
        @JvmField val DSTORE  = VarInsnOp(57, D, null, "DSTORE")
        @JvmField val ASTORE  = VarInsnOp(58, A, null, "ASTORE")

        @JvmField val RET     = VarInsnOp(169, W64Both, null, "RET")
    }
}
// ---------------------------------------------------------------
// Normal (non-inline) class for type-related opcodes (visitTypeInsn)
// ---------------------------------------------------------------
class TypeInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: SWord?,
    @JvmField val stackGiven: SWord,
    val asString: String
) {
    companion object {
        // … → A       (push new un-initialised object ref)
        @JvmField val NEW        = TypeInsnOp(187, null, A, "NEW")

        // I → A       (pop count, push reference to new array)
        @JvmField val ANEWARRAY  = TypeInsnOp(189, I, A, "ANEWARRAY")

        // A → A       (cast ref, leave it on stack)
        @JvmField val CHECKCAST  = TypeInsnOp(192, A, A, "CHECKCAST")

        // A → I       (pop ref, push int 0/1)
        @JvmField val INSTANCEOF = TypeInsnOp(193, A, I, "INSTANCEOF")
    }
}

// -----------------------------------------------------------------
// Normal class for field-access opcodes (visitFieldInsn)
// Width of the field value is unknown at this point → V1
// -----------------------------------------------------------------
class FieldInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: Array<SWord>,
    @JvmField val stackGiven: Array<SWord>,
    val asString: String
) {
    override fun equals(other: Any?): Boolean {
        return other is FieldInsnOp && other.opcode == opcode
    }

    companion object {
        // … → (v1)     (push static field)           v1 ∈ {I,A,L,D}
        @JvmField val GETSTATIC = FieldInsnOp(178, emptyArrayOfSWord, arrayOf(V1), "GETSTATIC")

        // (v1) → …     (pop value, store to static)  v1 ∈ {I,A,L,D}
        @JvmField val PUTSTATIC = FieldInsnOp(179, arrayOf(V1), emptyArrayOfSWord, "PUTSTATIC")

        // A → (v1)     (pop obj ref, push field)     v1 ∈ {I,A,L,D}
        @JvmField val GETFIELD  = FieldInsnOp(180, arrayOf(A), arrayOf(V1), "GETFIELD")

        // A (v1) → …   (pop obj ref + value, store)  v1 ∈ {I,A,L,D}
        @JvmField val PUTFIELD  = FieldInsnOp(181, arrayOf(A, V1), emptyArrayOfSWord, "PUTFIELD")
    }

    override fun hashCode(): Int {
        return opcode * 31
    }
}


class MethodInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: Array<SWord>,
    @JvmField val stackGiven: Array<SWord>,
    val asString: String
) {
    companion object {
        // (obj,args) → (ret)   - pops target + args, pushes ret (if any)
        @JvmField val INVOKEVIRTUAL   = MethodInsnOp(182, arrayOf(A, V), arrayOf(V1), "INVOKEVIRTUAL")

        // (obj,args) → (ret)   - used for <init>, private, super
        @JvmField val INVOKESPECIAL   = MethodInsnOp(183, arrayOf(A, V), arrayOf(V1), "INVOKESPECIAL")

        // (args) → (ret)       - static: no object reference
        @JvmField val INVOKESTATIC    = MethodInsnOp(184, arrayOf(V), arrayOf(V1), "INVOKESTATIC")

        // (obj,args) → (ret)   - poly dispatch via interface
        @JvmField val INVOKEINTERFACE = MethodInsnOp(185, arrayOf(A, V), arrayOf(V1), "INVOKEINTERFACE")
    }
}

// -----------------------------------------------------------------
// Normal class for conditional / jump opcodes (visitJumpInsn)
// -----------------------------------------------------------------
class JumpInsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: Array<SWord>,
    @JvmField val stackGiven: Array<SWord>,
    val asString: String
) {
    override fun equals(other: Any?): Boolean {
        if (other is JumpInsnOp) {
            return opcode == other.opcode
        }
        return false
    }

    override fun hashCode(): Int {
        return opcode.hashCode()
    }

    override fun toString(): String {
        return asString
    }
    companion object {
        // --- single-int compares ---
        // I → … (branch if ==0)
        @JvmField val IFEQ = JumpInsnOp(153, arrayOf(I), emptyArrayOfSWord, "IFEQ")
        // I → … (branch if !=0)
        @JvmField val IFNE = JumpInsnOp(154, arrayOf(I), emptyArrayOfSWord, "IFNE")
        // I → … (branch if <0)
        @JvmField val IFLT = JumpInsnOp(155, arrayOf(I), emptyArrayOfSWord, "IFLT")
        // I → … (branch if >=0)
        @JvmField val IFGE = JumpInsnOp(156, arrayOf(I), emptyArrayOfSWord, "IFGE")
        // I → … (branch if >0)
        @JvmField val IFGT = JumpInsnOp(157, arrayOf(I), emptyArrayOfSWord, "IFGT")
        // I → … (branch if <=0)
        @JvmField val IFLE = JumpInsnOp(158, arrayOf(I), emptyArrayOfSWord, "IFLE")

        // --- int pair compares ---
        // I I → … (branch if ==)
        @JvmField val IF_ICMPEQ = JumpInsnOp(159, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPEQ")
        // I I → … (branch if !=)
        @JvmField val IF_ICMPNE = JumpInsnOp(160, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPNE")
        // I I → … (branch if <)
        @JvmField val IF_ICMPLT = JumpInsnOp(161, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPLT")
        // I I → … (branch if >=)
        @JvmField val IF_ICMPGE = JumpInsnOp(162, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPGE")
        // I I → … (branch if >)
        @JvmField val IF_ICMPGT = JumpInsnOp(163, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPGT")
        // I I → … (branch if <=)
        @JvmField val IF_ICMPLE = JumpInsnOp(164, arrayOf(I, I), emptyArrayOfSWord, "IF_ICMPLE")

        // --- reference pair compares ---
        // A A → … (branch if ref==)
        @JvmField val IF_ACMPEQ = JumpInsnOp(165, arrayOf(A, A), emptyArrayOfSWord, "IF_ACMPEQ")
        // A A → … (branch if ref!=)
        @JvmField val IF_ACMPNE = JumpInsnOp(166, arrayOf(A, A), emptyArrayOfSWord, "IF_ACMPNE")

        // --- unconditional / legacy ---
        // … → … (unconditional jump)
        @JvmField val GOTO = JumpInsnOp(167, emptyArrayOfSWord, emptyArrayOfSWord, "GOTO")
        // … → A (push return address, jump to subroutine)
        @JvmField val JSR  = JumpInsnOp(168, emptyArrayOfSWord, arrayOf(A), "JSR")

        // --- null checks ---
        // A → … (branch if null)
        @JvmField val IFNULL    = JumpInsnOp(198, arrayOf(A), emptyArrayOfSWord, "IFNULL")
        // A → … (branch if non-null)
        @JvmField val IFNONNULL = JumpInsnOp(199, arrayOf(A), emptyArrayOfSWord, "IFNONNULL")
    }
}