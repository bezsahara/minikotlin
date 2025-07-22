@file:Suppress("SpellCheckingInspection")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.MethodInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBMethodCallOP
import org.bezsahara.minikotlin.lan.compiler.JvmInvoke
import org.bezsahara.minikotlin.lan.compiler.describeKFunction
import org.bezsahara.minikotlin.lan.compiler.getFunctionOwner
import org.bezsahara.minikotlin.lan.compiler.jvmInvoke
import kotlin.reflect.KFunction

// Specify boxable integer as nullable
// Currently works strange
fun <T: Function<*>> KBMethod.invokeFun(f: T) {
    f as KFunction<*>
    val jvmInvoke = f.jvmInvoke()
    val owner = getFunctionOwner(f) ?: error("Could not get function owner")
    val descriptor = describeKFunction(f, jvmInvoke, owner)
    when (jvmInvoke) {
        JvmInvoke.INVOKESTATIC -> invokestatic(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKEINTERFACE -> invokeinterface(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKESPECIAL -> invokespecial(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKEVIRTUAL -> invokevirtual(TypeInfo.Java(owner), f.name, descriptor)
    }
}

fun KBMethod.invokevirtual(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKEVIRTUAL,
        owner,
        name,
        descriptor,
        isInterface //isInterface
    ))
}

fun KBMethod.invokespecial(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKESPECIAL,
        owner,
        name,
        descriptor,
        isInterface
    ))
}

fun KBMethod.invokestatic(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKESTATIC,
        owner,
        name,
        descriptor,
        isInterface = isInterface
    ))
}

fun KBMethod.invokeinterface(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = true
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKEINTERFACE,
        owner,
        name,
        descriptor,
        isInterface = isInterface
    ))
}