package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.IntInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBSingleIntOP
import org.objectweb.asm.Opcodes
import kotlin.reflect.KClass

fun getArrayTypeFromClass(cls: KClass<out Any>): Int {
    return when (cls) {
        Int::class    -> Opcodes.T_INT
        Float::class  -> Opcodes.T_FLOAT
        Double::class -> Opcodes.T_DOUBLE
        Long::class   -> Opcodes.T_LONG
        Short::class  -> Opcodes.T_SHORT
        Byte::class   -> Opcodes.T_BYTE
        Char::class   -> Opcodes.T_CHAR //Kotlin sees char as not a number
        Boolean::class -> Opcodes.T_BOOLEAN
        else -> error("Unsupported primitive type for newarray: $cls")
    }
}

fun getArrayTypeFromClass(cls: Class<out Any>): Int {
    return when (cls) {
        Int::class.java    -> Opcodes.T_INT
        Float::class.java  -> Opcodes.T_FLOAT
        Double::class.java -> Opcodes.T_DOUBLE
        Long::class.java   -> Opcodes.T_LONG
        Short::class.java  -> Opcodes.T_SHORT
        Byte::class.java   -> Opcodes.T_BYTE
        Char::class.java   -> Opcodes.T_CHAR
        Boolean::class.java -> Opcodes.T_BOOLEAN
        else -> error("Unsupported primitive type for newarray: $cls")
    }
}

// Kotlin does not understand that Char is also a Number, so use this
fun KBMethod.newarrayOfChar() {
    addOperation(KBSingleIntOP(
        IntInsnOp.NEWARRAY,
        Opcodes.T_CHAR,
        TypeInfo.Java(CharArray::class.java)
    ))
}

// Same situation as char
fun KBMethod.newarrayOfBoolean() {
    addOperation(KBSingleIntOP(
        IntInsnOp.NEWARRAY,
        Opcodes.T_BOOLEAN,
        TypeInfo.Java(BooleanArray::class.java)
    ))
}

inline fun <reified T: Number> KBMethod.newarray() {
    addOperation(KBSingleIntOP(
        IntInsnOp.NEWARRAY,
        getArrayTypeFromClass(T::class),
        TypeInfo.KArray(TypeInfo.Kt(T::class))
    ))
}

fun KBMethod.newarray(cl: KClass<out Any>) {
    addOperation(KBSingleIntOP(
        IntInsnOp.NEWARRAY,
        getArrayTypeFromClass(cl),
        TypeInfo.KArray(TypeInfo.Kt(cl))
    ))
}

fun KBMethod.newarray(cl: Class<out Any>) {
    addOperation(KBSingleIntOP(
        IntInsnOp.NEWARRAY,
        getArrayTypeFromClass(cl),
        TypeInfo.KArray(TypeInfo.Java(cl))
    ))
}

fun KBMethod.sipush(v: Short) {
    addOperation(KBSingleIntOP(
        IntInsnOp.SIPUSH,
        v.toInt(),
        null
    ))
}

fun KBMethod.bipush(v: Byte) {
    addOperation(KBSingleIntOP(
        IntInsnOp.BIPUSH,
        v.toInt(),
        null
    ))
}