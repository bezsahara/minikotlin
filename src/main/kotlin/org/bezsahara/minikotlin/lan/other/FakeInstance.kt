package org.bezsahara.minikotlin.lan.other

import java.lang.reflect.Proxy

fun <T: Any> dummyInstance(clazz: Class<T>): T {
    return Proxy.newProxyInstance(
        clazz.classLoader,
        arrayOf(clazz)
    ) { _, _, _ -> null } as T
}

inline fun <reified T> dummyInstance(): T {
    return Proxy.newProxyInstance(
        T::class.java.classLoader,
        arrayOf(T::class.java)
    ) { _, _, _ -> null } as T
}

/**
 * Purpose of this class is to give interfaces a fake instance.
 * Useful for IDE type suggestion specifically for when using `thisFun.call` method in MiniKotlin.
 * That is because when you use implOf(SomeClass::someFunction) and someFunction accepts 2 arguments, kotlin will specify that
 * someFunction actually accepts 3 arguments 1st being the instance of SomeClass. While it is indeed correct, thisFun is designed not to accept instance arguments
 * @see org.bezsahara.minikotlin.lan.ThisFun
 */
abstract class AbstractInstanceObject<T> {
    private fun getOuterClass(): Class<out T> {
        return this::class.java.enclosingClass as? Class<T> ?: error("This class is not inner object")
    }

    val instance: T by lazy {
        dummyInstance(getOuterClass())
    }

    operator fun invoke(): T {
        return instance
    }
}