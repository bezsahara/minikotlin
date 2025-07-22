package org.bezsahara.minikotlin.builder.opcodes.method

class ByteCodeSave() {
    private val sb = StringBuilder()

    fun addEntry(prefix: String, value: String) {
        sb.append(prefix.length.toLong() + value.length.toLong())
        sb.append(prefix)
        sb.append(value)
    }

    override fun toString(): String {
        return sb.toString()
    }
}



fun saveByteCode(kbByteCodes: Array<KBByteCode>): String {
    val bs = ByteCodeSave()
    kbByteCodes.forEach {
        val byteCode = if (it is KBByteCode.Debug) it.value else it
        when (byteCode) {
            is KBFieldOP -> TODO()
            is KBIincOP -> TODO()
            is KBJumpOP -> {
                bs.addEntry("j", byteCode.save())
            }
            is KBLdcOP -> {
                bs.addEntry("l", byteCode.save())
            }
            is KBLookupSwitchOP -> {
                bs.addEntry("y", byteCode.save())
            }
            is KBMethodCallOP -> {
                bs.addEntry("m", byteCode.save())
            }
            is KBSingleIntOP -> {
                bs.addEntry("i", byteCode.save())
            }
            is KBSingleOP -> {
                bs.addEntry("s", byteCode.save())
            }
            is KBTableSwitchOP -> TODO()
            is KBTryCatchBlockOP -> TODO()
            is KBTypeOP -> TODO()
            is KBVariableOP -> bs.addEntry("v", byteCode.save())
            is LabelPoint -> {
                bs.addEntry("p", byteCode.save())
            }
            is KBAsmOp -> TODO()
            else -> Unit // Ignore
        }
    }

    return bs.toString()
}