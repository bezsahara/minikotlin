package org.bezsahara.minikotlin.builder.declaration

import org.objectweb.asm.Opcodes
import java.util.*

fun descriptorOf(clazz: Class<*>): String = when {
    clazz === Boolean::class.java -> "Z"
    clazz === Byte::class.java    -> "B"
    clazz === Char::class.java    -> "C"
    clazz === Short::class.java   -> "S"
    clazz === Int::class.java     -> "I"
    clazz === Long::class.java    -> "J"
    clazz === Float::class.java   -> "F"
    clazz === Double::class.java  -> "D"
    clazz === Void.TYPE           -> "V"
    else -> throw IllegalArgumentException("Not a primitive type: $clazz")
}

val primitiveTypeDescriptors = IdentityHashMap<Class<*>, String>().also {
    it[Boolean::class.java] = "Z"
    it[Byte::class.java] = "B"
    it[Char::class.java] = "C"
    it[Short::class.java] = "S"
    it[Int::class.java] = "I"
    it[Long::class.java] = "J"
    it[Float::class.java] = "F"
    it[Double::class.java] = "D"
    it[Void.TYPE] = "V"
}

fun DeclarationProperty<*, *>.getASMOpcodes(): Int {
    var flags = visibility.asmOpcode

    if (isFinal)        flags = flags or Opcodes.ACC_FINAL
    if (isSynchronized) flags = flags or Opcodes.ACC_SYNCHRONIZED
    if (isStatic)       flags = flags or Opcodes.ACC_STATIC
    if (isAbstract)     flags = flags or Opcodes.ACC_ABSTRACT
    if (isNative)       flags = flags or Opcodes.ACC_NATIVE
    if (isStrictfp)     flags = flags or Opcodes.ACC_STRICT
    if (isDefault)      error("Interfaces are not implemented")
    if (isVolatile)     flags = flags or Opcodes.ACC_VOLATILE
    if (isTransient)    flags = flags or Opcodes.ACC_TRANSIENT

    return flags
}