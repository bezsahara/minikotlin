package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

class ThrowValue(
    ref: KRef<*>,
) : KValue.ValueBlock(arrayOf(ref)) {
    override fun KBMethod.init(variables: Map<String, Int>, stackInfo: StackInfo) {
        athrow()
    }
}

class ReturnPiece(
    val ref: KRef<*>?,
) : KValue.ValueBlock(ref?.let { arrayOf(it) } ?: emptyArray()) {
    override fun KBMethod.init(variables: Map<String, Int>, stackInfo: StackInfo) {
        when (ref) {
            null -> return_() // If null then it is void
            is KRef.Native<*> -> {
                when (ref.kClass) {
                    Int::class, Short::class, Byte::class, Boolean::class, Char::class -> ireturn()
                    Long::class -> lreturn()
                    Double::class -> dreturn()
                    Float::class -> freturn()
                    else -> error("Unknown return type")
                }
            }

            is KRef.Obj<*> -> areturn()
            else -> error("Cannot determine return type for $ref")
        }
    }
}