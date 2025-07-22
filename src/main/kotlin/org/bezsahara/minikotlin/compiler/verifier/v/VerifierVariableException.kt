package org.bezsahara.minikotlin.compiler.verifier.v

import org.bezsahara.minikotlin.compiler.verifier.VerifierException

abstract class VerifierVariableException : VerifierException() {
    override fun getStackTrace(): Array<out StackTraceElement?>? {
        return emptyArray()
    }
}