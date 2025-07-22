package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode

class SWordDebug(
    val kind: SWord,
    val origin: KBByteCode,
)