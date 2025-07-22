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