package org.bezsahara.minikotlin.compiler

import org.bezsahara.minikotlin.builder.KBClass

interface KBCompiler {
    val version: Int

    fun compileClass(kbClassResult: KBClass.Result): ByteArray
}