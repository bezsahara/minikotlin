package org.bezsahara.minikotlin.builder.opcodes.codes

import org.bezsahara.minikotlin.compiler.verifier.SWordDebug

fun figureOutActualArrayLoad(ref: SWordDebug): Class<*> {
    val kind = ref.kind
    if (kind !is SWord.A) error("Kind is not even an array!")

    if (kind.clazz.isArray) {
        return kind.clazz.componentType
    }
    error("Not an array!")
}