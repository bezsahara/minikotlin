package org.bezsahara.minikotlin.compiler.verifier.v

import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode
import org.bezsahara.minikotlin.builder.opcodes.method.KBVariableOP

class VerifierVariableUninitializedException(
    val variable: KBByteCode,
    val allByteCode: List<KBByteCode>,
    override val cause: Throwable?,
    val funName: String
) : VerifierVariableException() {

    fun buildStringForWrongLoad(): String {
        val sb = StringBuilder()

        val actual = variable.actual()
        if (actual !is KBVariableOP) error("Not a variable! $actual")

        sb.append("In function $funName")
        sb.append("\nAttempted at loading uninitialized!\n")
        sb.append("Operation ")
        sb.append(actual.toString())
            .append(" expected ")
            .append(actual.instruction.stackGiven!!)
            .append(" but at index ")
            .append(actual.variableIndex)
            .append(" nothing was stored!")
        sb.append("\n\n")

        sb.append("Problem access:\n")

        val varIndexInCode = allByteCode.indexOf(variable)

        if (varIndexInCode == -1) error("Index is -1")


        sb.append(actual.toString())
            .append(" at index ")
            .append(varIndexInCode)

        sb.append("        ..(")
            .append(getPositionOfByteCode(variable))
            .append(")\n\n")

        return sb.toString()
    }

    override val message: String? by lazy {
        buildStringForWrongLoad()
    }
}