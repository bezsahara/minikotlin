@file:Suppress("SpellCheckingInspection")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.VarInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBIincOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBVariableOP

open class VarIndex<T>(val idx: Int) {
    class Ref(idx: Int) : VarIndex<Any>(idx)
    class Native<T: Number>(idx: Int) : VarIndex<T>(idx)
}

fun KBMethod.iload(varIndex: VarIndex.Native<Int>) = iload(varIndex.idx)
fun KBMethod.lload(varIndex: VarIndex.Native<Long>) = lload(varIndex.idx)
fun KBMethod.fload(varIndex: VarIndex.Native<Float>) = fload(varIndex.idx)
fun KBMethod.dload(varIndex: VarIndex.Native<Double>) = dload(varIndex.idx)
fun KBMethod.aload(varIndex: VarIndex.Ref) = aload(varIndex.idx)

fun KBMethod.istore(varIndex: VarIndex.Native<Int>) = istore(varIndex.idx)
fun KBMethod.lstore(varIndex: VarIndex.Native<Long>) = lstore(varIndex.idx)
fun KBMethod.fstore(varIndex: VarIndex.Native<Float>) = fstore(varIndex.idx)
fun KBMethod.dstore(varIndex: VarIndex.Native<Double>) = dstore(varIndex.idx)
fun KBMethod.astore(varIndex: VarIndex.Ref) = astore(varIndex.idx)

fun KBMethod.iload(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null)  { addOperation(KBVariableOP(VarInsnOp.ILOAD, varIndex, name, descriptor)) }
fun KBMethod.lload(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null)  { addOperation(KBVariableOP(VarInsnOp.LLOAD, varIndex, name, descriptor)) }
fun KBMethod.fload(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null)  { addOperation(KBVariableOP(VarInsnOp.FLOAD, varIndex, name, descriptor)) }
fun KBMethod.dload(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null)  { addOperation(KBVariableOP(VarInsnOp.DLOAD, varIndex, name, descriptor)) }
fun KBMethod.aload(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null)  { addOperation(KBVariableOP(VarInsnOp.ALOAD, varIndex, name, descriptor)) }

// I mean it should at index 0, right?
fun KBMethod.loadThis() {
    if (methodProperty.isStatic) error("Cannot load this in static method!")
    addOperation(KBVariableOP(VarInsnOp.ALOAD, 0))
}

fun KBMethod.istore(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null) { addOperation(KBVariableOP(VarInsnOp.ISTORE, varIndex, name, descriptor)) }
fun KBMethod.lstore(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null) { addOperation(KBVariableOP(VarInsnOp.LSTORE, varIndex, name, descriptor)) }
fun KBMethod.fstore(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null) { addOperation(KBVariableOP(VarInsnOp.FSTORE, varIndex, name, descriptor)) }
fun KBMethod.dstore(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null) { addOperation(KBVariableOP(VarInsnOp.DSTORE, varIndex, name, descriptor)) }
fun KBMethod.astore(varIndex: Int, name: String? = null, descriptor: TypeInfo? = null) { addOperation(KBVariableOP(VarInsnOp.ASTORE, varIndex, name, descriptor)) }

fun KBMethod.ret(varIndex: Int)    { addOperation(KBVariableOP(VarInsnOp.RET, varIndex)) }

//

fun KBMethod.iinc(varIndex: Int, increment: Int) { addOperation(KBIincOP(varIndex, increment)) }