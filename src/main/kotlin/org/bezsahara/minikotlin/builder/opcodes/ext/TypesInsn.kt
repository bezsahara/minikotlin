package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.TypeInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBTypeOP

fun KBMethod.new(typeInfo: TypeInfo) {
    addOperation(KBTypeOP(TypeInsnOp.NEW, typeInfo))
}

@Suppress("SpellCheckingInspection")
fun KBMethod.anewarray(typeInfo: TypeInfo) {
    addOperation(KBTypeOP(TypeInsnOp.ANEWARRAY, typeInfo))
}

@Suppress("SpellCheckingInspection")
fun KBMethod.checkcast(typeInfo: TypeInfo) {
    addOperation(KBTypeOP(TypeInsnOp.CHECKCAST, typeInfo))
}

fun KBMethod.instanceOf(typeInfo: TypeInfo) {
    addOperation(KBTypeOP(TypeInsnOp.INSTANCEOF, typeInfo))
}

fun KBMethod.newAndInit(typeInfo: TypeInfo) {
    new(typeInfo)
    dup()
    invokespecial(typeInfo, "<init>", MDInfo.EmptyWithVoid)
}