package org.bezsahara.minikotlin.lan.logic.math

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.iinc
import org.bezsahara.minikotlin.builder.opcodes.ext.iload
import org.bezsahara.minikotlin.lan.*
import org.bezsahara.minikotlin.lan.pieces.MathOp
import org.bezsahara.minikotlin.lan.pieces.MathOperation


class IINCOperation(val ref: KVar.Native<Int>, val byNumber: Byte): KValue.ValueBlock(null) {
    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val i = variables[ref.name]!!
        iinc(
            i,
            byNumber.toInt()
        )
    }
}

// TODO impl later
class IINCOperationReturns(val ref: KVar.Native<Int>, val byNumber: Byte): KValue.ValueBlockReturns() {
    override val objType: TypeInfo = TypeInfo.Int

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val i = variables[ref.name]!!
        iinc(
            i,
            byNumber.toInt()
        )
        iload(i)
    }
}


context(mk: MiniKotlin<*>)
fun KVar.Native<Int>.incrementBy(b: Byte) {
    mk.performAction(IINCOperation(this, b))
}

//context(mk: MiniKotlin<*>)
//operator fun KVar.Native<Int>.inc(by: Byte) {
//    mk.performAction(IINCOperation(this, by))
//}
//context(mk: MiniKotlin<*>)
//operator fun KVar.Native<Int>.dec(): KVar.Native<Int> {
//    mk.performAction(IINCOperation(this, by))
//}

context(mk: MiniKotlin<*>)
operator fun <T: Number> KRef.Native<T>.plus(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.Add
    ))
}

context(mk: MiniKotlin<*>)
operator fun <T: Number> KRef.Native<T>.minus(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(
        other.jClass, MathOperation(
            this,
            other,
            MathOp.Subtract,
        )
    )
}

context(mk: MiniKotlin<*>)
operator fun <T: Number> KRef.Native<T>.div(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.Divide,
    ))
}

context(mk: MiniKotlin<*>)
operator fun <T: Number> KRef.Native<T>.times(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.Multiply,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.and(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.And,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.or(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.Or,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.xor(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.Xor,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.shl(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.ShiftLeft,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.shr(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.ShiftRight,
    ))
}

context(mk: MiniKotlin<*>)
infix fun <T: Number> KRef.Native<T>.ushr(other: KRef.Native<T>): KRef.Native<T> {
    return KRef.Native(other.jClass, MathOperation(
        this,
        other,
        MathOp.UnsignedShiftRight,
    ))
}