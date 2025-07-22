package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo
import java.lang.reflect.Modifier
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.KProperty
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaSetter


data class PropertyGet(
    val prop: KProperty<*>,
    val owner: TypeInfo,
    val name124: String,
    val returnType: TypeInfo,
    val instance: KRef<*>?,
    val callType: Int,
    val asNative: Boolean,
) : KValue.ValueBlockReturns(
    instance
) {
    // callTypes:
    // 1 - getter
    // 2 - field
    // _3 - static
    // _4 - virtual
    // 15 - interface
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        when (callType) {
            13 -> {
                invokestatic(owner, name124, MDInfo(emptyArray(), returnType))
            }

            14 -> {
                invokevirtual(owner, name124, MDInfo(emptyArray(), returnType))
            }

            23 -> getstatic(owner, name124, returnType)
            24 -> getfield(owner, name124, returnType)
            15 -> invokeinterface(owner, name124, MDInfo(emptyArray(), returnType))
        }
    }

    override val objType: TypeInfo = returnType
}

data class PropertySet(
    val prop: KMutableProperty<*>,
    val fieldName: String,
    val value: KRef<*>,
    val asNative: Boolean,
    val instance: KRef<*>?,
) : KValue.ValueBlock(
    if (instance == null) {
        arrayOf(value)
    } else {
        arrayOf(value, instance)
    }
) {
    override val autoPush: Boolean = false

    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val setter = prop.javaSetter
        val field = prop.javaField

        val owner = (prop as kotlin.jvm.internal.CallableReference).owner
        val objInstance = (owner as KClass<*>).objectInstance

        val tpOwner = TypeInfo.Java(owner.java)
        if (instance == null && objInstance != null) {
            getstatic(tpOwner, "INSTANCE", tpOwner)
        } else {
            stackInfo.pushArgument(this, 1)
        }
        stackInfo.pushArgument(this, 0)
        when {
            setter != null -> {
                val arg = setter.parameterTypes.firstOrNull() ?: error("Could not find a single argument for setter")
                if (Modifier.isStatic(setter.modifiers)) {
                    invokestatic(tpOwner, setter.name, args(arg))
                } else {
                    invokevirtual(tpOwner, setter.name, args(arg))
                }
            }

            field != null -> {
                if (Modifier.isStatic(field.modifiers)) {
                    getstatic(tpOwner, field.name, TypeInfo.Java(field.type))
                } else {
                    getfield(tpOwner, field.name, TypeInfo.Java(field.type))
                }
            }
        }
    }
}
