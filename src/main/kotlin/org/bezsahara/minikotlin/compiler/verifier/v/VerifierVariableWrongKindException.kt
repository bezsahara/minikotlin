package org.bezsahara.minikotlin.compiler.verifier.v

import org.bezsahara.minikotlin.builder.opcodes.method.JustPrint
import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode
import org.bezsahara.minikotlin.builder.opcodes.method.KBVariableOP

class VerifierVariableWrongKindException(
    val variable: KBByteCode,
    val originalVariable: KBByteCode,
    val wholeStack: List<KBByteCode>,
    override val cause: Throwable?,
    val isW64Occupation: Boolean,
    val funName: String
) : VerifierVariableException() {

    fun buildStringForWrongLoad(): String {
        val sb = StringBuilder()

        val originalActual = originalVariable.actual()
        val actual = variable.actual()
        if (actual !is KBVariableOP) error("Not a variable! $actual")

        var paramMode = false

        val oAIndex: Int
        val oASWord: String
        if (originalActual !is KBVariableOP) {
            if (originalActual is JustPrint && (originalActual.marker is String)) {
                val marker = originalActual.marker
                if (!marker.startsWith("param") && !marker.startsWith("this")) {
                    error("Not a variable! $actual")
                }
                val all = marker.split(' ')
                oAIndex = all[2].toInt()
                oASWord = all[1]
                paramMode = true
            } else {
                error("Not a variable! $actual")
            }
        } else {
            oAIndex = originalActual.variableIndex
            oASWord = originalActual.instruction.stackTaken!!.toString()
        }

        if (!isW64Occupation) {
            sb.append("In Function `$funName`")
            sb.append("\nAttempted at loading wrong type!\n")
            sb.append("Operation ")
            sb.append(actual.toString())
                .append(" expected ")
                .append(actual.instruction.stackGiven!!)
                .append(" but index ")
                .append(actual.variableIndex)
                .append(" stored ")
                .append(oASWord)
            sb.append("\n\n")
        } else {
            sb.append("\nAttempted at overwriting second part of cat-2 value!\n")
            sb.append("Operation ")
            sb.append(actual.toString())
                .append(" tries to store at index ")
                .append(actual.variableIndex)
                .append(" but at index $oAIndex ")
                .append(oASWord)
                .append(" was stored!")
            sb.append("\n\n")
        }

        sb.append("Problem access:\n")

        val varIndexInCode = wholeStack.indexOf(variable)
        val originalVarIndexInCode = wholeStack.indexOf(originalVariable)

        if (varIndexInCode == -1) error("Index is -1")

        if (originalVarIndexInCode == -1 && !paramMode) error("Index is -1")

        sb.append(actual.toString())
            .append(" at index ")
            .append(varIndexInCode)

        sb.append("        ..(")
            .append(getPositionOfByteCode(variable))
            .append(")\n\n")

        if (!isW64Occupation) {
            sb.append("Last store in that index:\n")
        } else {
            sb.append("W64 store in that index-1:\n")
        }
        sb.append(originalActual.toString())

        if (paramMode) {
            sb.append(" as parameter of the function!")
        } else {
            sb.append(" at index ")
                .append(originalVarIndexInCode)
        }

        sb.append("        ..(")
            .append(getPositionOfByteCode(originalVariable))
            .append(")\n\n")

        return sb.toString()
    }

    override val message: String? by lazy {
        buildStringForWrongLoad()
    }
}