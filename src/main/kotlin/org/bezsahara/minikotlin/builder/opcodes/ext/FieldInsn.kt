@file:Suppress("SpellCheckingInspection")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.FieldInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBFieldOP

//GETSTATIC, PUTSTATIC, GETFIELD or PUTFIELD.
fun KBMethod.getfield(
    owner: TypeInfo,
    name: String,
    descriptor: TypeInfo
) {
    addOperation(KBFieldOP(
        FieldInsnOp.GETFIELD,
        owner,
        name,
        descriptor
    ))
}

fun KBMethod.putfield(
    owner: TypeInfo,
    name: String,
    descriptor: TypeInfo
) {
    addOperation(KBFieldOP(
        FieldInsnOp.PUTFIELD,
        owner,
        name,
        descriptor
    ))
}

fun KBMethod.getstatic(
    owner: TypeInfo,
    name: String,
    descriptor: TypeInfo
) {
    addOperation(KBFieldOP(
        FieldInsnOp.GETSTATIC,
        owner,
        name,
        descriptor
    ))
}

fun KBMethod.putstatic(
    owner: TypeInfo,
    name: String,
    descriptor: TypeInfo
) {
    addOperation(KBFieldOP(
        FieldInsnOp.PUTSTATIC,
        owner,
        name,
        descriptor
    ))
}

