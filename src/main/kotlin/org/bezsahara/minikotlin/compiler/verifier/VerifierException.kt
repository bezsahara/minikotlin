package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode

abstract class VerifierException : RuntimeException() {
    protected var noSourceEncountered = false
    protected fun getPositionOfByteCode(byteCode: KBByteCode): String {
        if (byteCode is KBByteCode.Debug) {
            val debugInfo = byteCode.debugInfo
            val stackTrace = debugInfo.stackTrace
            val idx = stackTrace.indexOfLast { it.className.startsWith("org.bezsahara.minikotlin.builder.opcodes.ext") }
//            val idx = stackTrace.indexOfLast { it.methodName == "addOperation" }
            val element = stackTrace[idx + 1]
            return "${element.fileName}:${element.lineNumber}"
        }
        noSourceEncountered = true
        return "No source"
    }
}