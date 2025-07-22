package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.lan.MiniKotlinAny
import org.bezsahara.minikotlin.lan.runsMiniKt
import java.util.*
import kotlin.reflect.KClass

interface JustATest {
//    fun
}

inline fun <reified T: Any> makeTestClassAndCrash(
    n: String = UUID.randomUUID().toString().replace("-", ""),
    func: Function<*>,
    noinline block: MiniKotlinAny.() -> Unit,
): KBClass.Result {
    val cl = makeClass(n) implements T::class body {
        autoInit()

        implOf(func).runsMiniKt(block)
    }
    return cl.result()
}


fun <T: Any> makeTestClass(
    func: Function<*>,
    cl: KClass<T>,
    block: MiniKotlinAny.() -> Unit,
): KBClass.Result {
    val cl = makeClass(UUID.randomUUID().toString().replace("-", "")) implements cl body {
        autoInit()

        implOf(func).runsMiniKt(block)
    }
    return cl.result()
}

inline fun <reified T: Any> makeTestClass(
    func: Function<*>,
    noinline block: MiniKotlinAny.() -> Unit,
): KBClass.Result {
    return makeTestClass(func, T::class, block)
}
