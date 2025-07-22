package org.bezsahara.minikotlin.builder.opcodes.method

import org.bezsahara.minikotlin.builder.opcodes.DebugInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord

interface NotFunctional

sealed class KBByteCode {
    // Used to point to source of the problem. You can disable it.
    data class Debug(val value: KBByteCode, val debugInfo: DebugInfo) : KBByteCode() {
        override fun justInsnName(): String {
            return value.justInsnName()
        }

        override fun toString(): String {
            return "!D: $value"
        }

        override fun getBytesSize(currentOffset: Int): Int {
            return value.getBytesSize(currentOffset)
        }

        override fun actual(): KBByteCode {
            return value
        }
    }

    abstract fun justInsnName(): String

    abstract fun getBytesSize(currentOffset: Int): Int

    open fun actual(): KBByteCode {
        return this
    }
}

sealed class ByteCodeMetaData : KBByteCode(), NotFunctional {
    override fun justInsnName(): String {
        return ""
    }

    override fun getBytesSize(currentOffset: Int): Int {
        return -1
    }
    //    data class VariableReferenceDeath(val index: Int, val kind: SWord) : ByteCodeMetaData()
}


open class JustPrint(val s: String, val marker: Any? = null) : KBByteCode(), NotFunctional {
    constructor(): this("JustPrintObj-NoStringProvided")

    override fun toString(): String {
        return s
    }

    override fun justInsnName(): String {
        return toString()
    }

    override fun getBytesSize(currentOffset: Int): Int {
        return 0
    }
}

abstract class DebugStack : KBByteCode(), NotFunctional {
    override fun getBytesSize(currentOffset: Int): Int {
        return 0
    }

    abstract fun debug(stack: List<SWord>)

    override fun justInsnName(): String {
        return "NONE"
    }

    companion object {
        operator fun invoke(name: String?): DebugStack {
            return DebugStackImpl(name)
        }
    }
}


class DebugStackImpl(val name: String?) : DebugStack() {
    override fun debug(stack: List<SWord>) {
        println("Current stack${if (name != null) " [$name]" else ""}: $stack")
    }
}
