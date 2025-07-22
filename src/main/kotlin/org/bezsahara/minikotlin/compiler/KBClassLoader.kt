package org.bezsahara.minikotlin.compiler

class KBClassLoader(parent: ClassLoader? = ClassLoader.getSystemClassLoader()) : ClassLoader(parent) {
    fun define(className: String, bytecode: ByteArray): Class<*> {
        return defineClass(className, bytecode, 0, bytecode.size)
    }
}

data class KBClassPair(
    val classLoader: KBClassLoader,
    val clazz: Class<*>
)