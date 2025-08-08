package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.opcodes.method.KBTryCatchBlockOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label

fun KBMethod.autoInitAndReturn() {
    aload(0) // load this
    invokespecial(TypeInfo.Object, "<init>", args() returns TypeInfo.Void) // call init of its super class, that is Object
    return_() // return
}

fun KBMethod.autoInit() {
    aload(0) // load this
    invokespecial(TypeInfo.Object, "<init>", args() returns TypeInfo.Void) // call init of its super class, that is Object
}

fun KBMethod.autoInit(extends: TypeInfo) {
    aload(0) // load this
    invokespecial(extends, "<init>", args() returns TypeInfo.Void) // call init of its super class, that is Object
}

fun KBMethod.i_unbox() {
    invokevirtual(TypeInfo.Java(Int::class.javaObjectType), "intValue", args() returns Int::class)
}

fun KBMethod.tryCatchBlock(
    start: Label,
    end: Label,
    handler: Label,
    errType: TypeInfo
) {
    addOperation(KBTryCatchBlockOP(
        start,
        end,
        handler,
        errType
    ))
}