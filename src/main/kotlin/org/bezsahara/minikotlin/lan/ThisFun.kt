package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.builder.*
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.compiler.asm.mapA
import kotlin.reflect.KClass


class ThisFun<T: Any>(val mk: MiniKotlin<T>) {
    val methodProperty = mk.methodProperty
    val descriptor = MDInfo(
        mk.params.mapA { it.typeInfo },
        methodProperty.typeInfo!!
    )

    val ThisClass = mk.kbClass.ThisClass

    inner class ThisFunMethodCall(val thisMethod: ThisMethodAny, val args: Array<KRef<*>>) : KValue.ValueBlockReturns(args) {
        override val autoPush: Boolean = false

        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            val property = thisMethod.methodDeclarationProperty
            if (!property.isStatic) {
                aload(0)
            }

            repeat(args.size) {
                stackInfo.pushArgument(it)
            }

            when {
                property.isStatic -> {
                    invokestatic(ThisClass, thisMethod.name, thisMethod.methodDescriptor())
                }
                property.visibility == Visibility.Private -> {
                    invokespecial(ThisClass, thisMethod.name, thisMethod.methodDescriptor())
                }
                property.isFinal -> {
                    invokevirtual(ThisClass, thisMethod.name, thisMethod.methodDescriptor())
                }
                else -> error("")
            }
        }

        override val objType: TypeInfo = thisMethod.methodDeclarationProperty.typeInfo!!
    }

    // No need to pass itself in args if it is non-static
    fun <T: Any> callMethod(thisMethod: ThisMethodAny, kl: KClass<out T>, asNative: Boolean, args: Array<KRef<*>>): KRef<T> {
        if (!thisMethod.methodDeclarationProperty.isStatic) {
            require(!methodProperty.isStatic) { "Cannot call non static methods from static methods." }
        }

        val methodArgs = thisMethod.parameters

        if (methodArgs.size != args.size) error("Different arguments for the method")

        methodArgs.forEachIndexed { index, parameter ->

            if (!parameter.typeInfo.recoverJClass().isAssignableFrom(args[index].jClass)) {
//                error("Wrong $index argument type.")
            }


        }

        val ksd = args
        if (asNative) {
            return KRef.Native(kl, ThisFunMethodCall(thisMethod, ksd))
        }

        return KRef.Obj(kl, ThisFunMethodCall(thisMethod, ksd))
    }

    inner class ThisFunFieldCallPut(val thisField: ThisField, val value: KRef<*>) : KValue.ValueBlock(arrayOf(value)) {
        override val autoPush: Boolean = false

        override fun KBMethod.init(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            if (!thisField.isStatic) {
                aload(0)
            }

            stackInfo.pushArgument(0)

            if (thisField.isStatic) {
                putstatic(mk.kbClass.ThisClass, thisField.name, thisField.typeInfo)
            } else {
                putfield(mk.kbClass.ThisClass, thisField.name, thisField.typeInfo)
            }
        }
    }

    inner class ThisFunFieldCallGet(val thisField: ThisField) : KValue.ValueBlockReturns() {
        override val autoPush: Boolean = true

        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            if (thisField.isStatic) {
                getstatic(mk.kbClass.ThisClass, thisField.name, thisField.typeInfo)
            } else {
                aload(0)
                getfield(mk.kbClass.ThisClass, thisField.name, thisField.typeInfo)
            }
        }

        override val objType: TypeInfo = thisField.typeInfo
    }

    fun <T: Any> getField(thisField: ThisField, asNative: Boolean): KRef<T> {
        if (asNative) {
            return KRef.Native(thisField.typeInfo.recoverJClass(), ThisFunFieldCallGet(thisField)) as KRef<T>
        }

        return KRef.Obj(thisField.typeInfo.recoverJClass(), ThisFunFieldCallGet(thisField)) as KRef<T>
    }

    fun setField(thisField: ThisField, value: KRef<*>){
        require(thisField.typeInfo.recoverJClass().isAssignableFrom(value.jClass)) {
            "Field cannot accept such value"
        }
        mk.performAction(ThisFunFieldCallPut(thisField, value))
    }

    fun insertCall(kbMethod: KBMethod) {
        when {
            methodProperty.isStatic -> {
                kbMethod.invokestatic(mk.kbClass.ThisClass, mk.methodName, descriptor)
            }
            methodProperty.visibility == Visibility.Private -> {
                kbMethod.invokespecial(mk.kbClass.ThisClass, mk.methodName, descriptor)
            }
            else -> {
                kbMethod.invokevirtual(mk.kbClass.ThisClass, mk.methodName, descriptor)
            }
        }
    }
}

context(mk: MiniKotlin<out Any>)
fun ThisField.setField(value: KRef<*>) {
    mk.thisFun.setField(this, value)
}

context(mk: MiniKotlin<out Any>)
fun <T: Any> ThisField.getField(): KRef.Obj<T> {
    return mk.thisFun.getField<T>(this, false) as KRef.Obj<T>
}

context(mk: MiniKotlin<out Any>)
fun <T: Any> ThisField.getFieldNt(): KRef.Native<T> {
    return mk.thisFun.getField<T>(this, true) as KRef.Native<T>
}

inline fun <reified T: Any> ThisFun<out Any>.callMethodNt(thisMethod: ThisMethodAny, vararg args: KRef<*>): KRef.Native<T> {
    return callMethod(thisMethod, T::class, true, args as Array<KRef<*>>) as KRef.Native<T>
}

inline fun <reified T: Any> ThisFun<out Any>.callMethod(thisMethod: ThisMethodAny, vararg args: KRef<*>): KRef.Obj<T> {
    return callMethod(thisMethod, T::class, false, args as Array<KRef<*>>) as KRef.Obj<T>
}

context(mk: MiniKotlin<out Any>)
inline fun <reified T: Any> ThisMethodAny.callMethod(vararg args: KRef<*>): KRef.Obj<T> {
    return mk.thisFun.callMethod(this, T::class, false, args as Array<KRef<*>>) as KRef.Obj<T>
}

context(mk: MiniKotlin<out Any>)
inline fun <reified T: Any> ThisMethodAny.callMethodNt(vararg args: KRef<*>): KRef.Native<T> {
    return mk.thisFun.callMethod(this, T::class, true, args as Array<KRef<*>>) as KRef.Native<T>
}


@JvmName("callMethodFN")
context(mk: MiniKotlin<out Any>)
inline fun <reified R: Any> ThisMethod<out Function<R>>.callMethod(vararg args: KRef<*>): KRef.Obj<R> {
    return mk.thisFun.callMethod(this, R::class, false, args as Array<KRef<*>>) as KRef.Obj<R>
}

@JvmName("callMethodFNNT")
context(mk: MiniKotlin<out Any>)
inline fun <reified R: Any> ThisMethod<out Function<R>>.callMethodNt(vararg args: KRef<*>): KRef.Native<R> {
    return mk.thisFun.callMethod(this, R::class, true, args as Array<KRef<*>>) as KRef.Native<R>
}
