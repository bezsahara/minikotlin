package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode
import org.bezsahara.minikotlin.builder.opcodes.method.LabelPoint

class VerifierStackException(
    val byteCode: KBByteCode,
    val actualStack: Array<SWordDebug?>,
    val neededStack: Array<SWord?>,
    val idxRT: Int,
    val wholeStack: List<KBByteCode>,
    val wrongIdx: Int,
    override val cause: Throwable,
    val stackDifference: Boolean,
    val funName: String
) : VerifierException() {
    private fun decide(): StringBuilder {
        val sb = StringBuilder()
//        neededStack.reverse()
        if (stackDifference) {
            val labelPoint = byteCode.actual()
            if (labelPoint !is LabelPoint) error("$labelPoint is not labelPoint. Should not happen probably")

            sb.append("In function $funName")
            sb.append("\nStack is different! At label ${labelPoint.label} {index: $idxRT}: expected ")
            if (neededStack.isNotEmpty()) {
                sb.append(neededStack.map { it?.toStringSpecial() })
            } else {
                sb.append("[]")
            }
            sb.append(", found ")
            if (actualStack.isNotEmpty()) {
                sb.append(actualStack.map { it?.kind?.toStringSpecial() })
            } else {
                sb.append("[]")
            }
            sb.append("\n\n")
        } else {
            sb.append("\nError at instruction ${byteCode.justInsnName()} {index: $idxRT}: expected ")
            if (neededStack.isNotEmpty()) {
                sb.append(neededStack.map { it?.toStringSpecial() })
            } else {
                sb.append("[]")
            }
            sb.append(", found ")
            if (actualStack.isNotEmpty()) {
                sb.append(actualStack.map { it?.kind?.toStringSpecial() })
            } else {
                sb.append("[]")
            }
            sb.append('\n')
            sb.append('\n')
        }

        sb.append("Stack (top to bottom):\n")
        val neededSize = neededStack.size
        val actualStackSlice = actualStack

        actualStackSlice.forEachIndexed { index, debug ->
            sb.append("  ")
            if (debug == null) {
                sb.append("nothing")
            } else {
                val codeIdx = wholeStack.indexOf(debug.origin)
                if (codeIdx == -1) {
                    error("Could not find index")
                }

                val previousIndex = sb.lastIndex
                sb.append('[')
                sb.append(index)
                sb.append("] ")
                val kind = debug.kind
                sb.append(kind)

                if (kind is SWord.A) {
                    sb.append(": ${kind.clazz.name}")
                }

                sb.append(" — ")
                sb.append("pushed at index $codeIdx by ")
                sb.append(debug.origin.actual().toString())
                val pieceL = sb.lastIndex - previousIndex
                repeat(50 - pieceL) {
                    sb.append(' ')
                }
                sb.append("..(")
                sb.append(getPositionOfByteCode(debug.origin))
                sb.append(")")
                if (!stackDifference && wrongIdx == index) {
                    sb.append("  <--- Wrong")
                }
                sb.append('\n')
            }
        }
        sb.append("\nInstruction:\n  ")
        sb.append(byteCode.actual().toString())
        sb.append(" {index: $idxRT} ..(")
        sb.append(getPositionOfByteCode(byteCode))
        sb.append(")\n")
        if (noSourceEncountered) {
            sb.append("\nIf you want to see source, set debug = true in ClassProperties")
        }
        return sb
    }

    override val message: String?
        get() {
            return decide().toString()
        }


    override fun getStackTrace(): Array<out StackTraceElement?>? {
        return null
    }
}