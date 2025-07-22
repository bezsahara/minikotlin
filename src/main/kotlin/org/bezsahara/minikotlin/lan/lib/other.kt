package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.checkcast
import org.bezsahara.minikotlin.builder.opcodes.ext.getstatic
import org.bezsahara.minikotlin.builder.opcodes.ext.instanceOf
import org.bezsahara.minikotlin.lan.*
import org.bezsahara.minikotlin.lan.logic.ReturnPiece
import org.bezsahara.minikotlin.lan.pieces.CustomActionPiece
import kotlin.reflect.KClass
import kotlin.reflect.KFunction

@Suppress("UNREACHABLE_CODE", "FunctionName")
fun MiniKotlin<*>.return_(n: Number) {
    return_(number(n))
}

@Suppress("UNREACHABLE_CODE", "FunctionName")
fun MiniKotlin<*>.return_(b: Boolean) {
    return_(bool(b))
}

@JvmName("return_K")
fun <T> ThisFun<out KFunction<T>>.return_(r: KRef<T>) {
    mk.addPiece(CustomActionPiece(ReturnPiece(r)))
}

fun <T> ThisFun<out Function<T>>.return_(r: KRef<T>) {
    mk.addPiece(CustomActionPiece(ReturnPiece(r)))
}

fun KRef.Obj<*>.asJavaObj(): KRef.Obj<Object> {
    return KRef.Obj(Object::class, kValueReturns(TypeInfo.Object) {
        checkcast(TypeInfo.Object)
    })
}

inline fun <reified T: Any> KRef.Obj<*>.castTo(): KRef.Obj<T> {
    return KRef.Obj(T::class, CheckCastValue(this, T::class.java))
}

class CheckCastValue(val ref: KRef<*>, cl: Class<*>) : KValue.ValueBlockReturns(ref) {
    override val objType: TypeInfo = TypeInfo.Java(cl)
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        checkcast(objType)
    }
}

fun KRef.Obj<*>.instanceOf(kClass: KClass<*>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, kValueReturns(TypeInfo.Boolean, arrayOf(this)) {
        instanceOf(TypeInfo.Java(kClass.javaObjectType))
    })
}

class ObjectRefValue(objClass: KClass<*>): KValue.ValueBlockReturns() {
    init {
        require(objClass.objectInstance != null) {
            "$objClass must be an object!"
        }
    }

    override val objType: TypeInfo = TypeInfo.Kt(objClass)
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        getstatic(objType, "INSTANCE", objType)
    }
}


inline fun <reified T: Any> objectRefOf(obj: T): ReusableRefObj<T> {
    return ReusableRefObj(T::class.java, ObjectRefValue(T::class))
}

@JvmName("retrun_ref")
fun MiniKotlin<*>.return_(r: KRef<*>) {
    performAction(ReturnPiece(r))
}

fun kS(a: Any) {
    val s = a::class.java
    s.name
}


fun sth() {
    val s = Int::class.java
    java.lang.Integer.TYPE
}