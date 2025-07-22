package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.getstatic
import kotlin.reflect.KClass
import kotlin.reflect.KType

@Suppress("UNCHECKED_CAST")
fun <T: Any> KType.getKClass(): KClass<T> {


    return classifier as? KClass<T> ?: error("Cannot get KClass of $this")
}

// Since Kotlin, as syntax sugar, but still very dumb, considers that object is "static" but in reality it is not,
// while being indicated as KProperty0.
fun objectInstanceGet(owner: TypeInfo): KValue {
    return object : KValue.ValueBlockReturns() {
        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            getstatic(owner, "INSTANCE", owner)
        }

        override val objType: TypeInfo = owner
    }
}