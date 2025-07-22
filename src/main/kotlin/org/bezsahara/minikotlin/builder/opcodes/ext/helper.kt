package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.returns

fun KBMethod.autoInit() {
    aload(0) // load this
    invokespecial(TypeInfo.Object, "<init>", args() returns TypeInfo.Void) // call init of its super class, that is Object
    return_() // return
}

fun KBMethod.i_unbox() {
    invokevirtual(TypeInfo.Java(Int::class.javaObjectType), "intValue", args() returns Int::class)
}