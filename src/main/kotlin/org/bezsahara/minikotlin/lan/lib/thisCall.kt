package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.aload
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo
import org.bezsahara.minikotlin.lan.ThisFun

class CallItself(
    val args: Array<KRef<*>>,
    val thisFun: ThisFun<*>,
) : KValue.ValueBlockReturns(args) {
    override val autoPush: Boolean = false

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        if (!thisFun.mk.methodProperty.isStatic) {
            aload(0)
        }
        repeat(args.size) {
            stackInfo.pushArgument(it)
        }

        thisFun.insertCall(this)
    }

    fun io() {}

    override val objType: TypeInfo = thisFun.descriptor.returns

}

inline fun <reified R : Any> ThisFun<out Function0<R>>.call(): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(emptyArray(), this))
}

inline fun <reified R : Any> ThisFun<out Function0<R>>.callNt(): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(emptyArray(), this))
}

inline fun <reified R : Any, A1 : Any> ThisFun<out Function1<A1, R>>.call1(arg1: KRef<A1>): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1), this))
}

inline fun <reified R : Any, A1 : Any> ThisFun<out Function1<A1, R>>.callNt1(arg1: KRef<A1>): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any> ThisFun<out Function2<A1, A2, R>>.call2(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any> ThisFun<out Function2<A1, A2, R>>.callNt2(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any> ThisFun<out Function3<A1, A2, A3, R>>.call3(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any> ThisFun<out Function3<A1, A2, A3, R>>.callNt3(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any> ThisFun<out Function4<A1, A2, A3, A4, R>>.call4(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any> ThisFun<out Function4<A1, A2, A3, A4, R>>.callNt4(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any> ThisFun<out Function5<A1, A2, A3, A4, A5, R>>.call5(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any> ThisFun<out Function5<A1, A2, A3, A4, A5, R>>.callNt5(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any> ThisFun<out Function6<A1, A2, A3, A4, A5, A6, R>>.call6(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any> ThisFun<out Function6<A1, A2, A3, A4, A5, A6, R>>.callNt6(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any> ThisFun<out Function7<A1, A2, A3, A4, A5, A6, A7, R>>.call7(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any> ThisFun<out Function7<A1, A2, A3, A4, A5, A6, A7, R>>.callNt7(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any> ThisFun<out Function8<A1, A2, A3, A4, A5, A6, A7, A8, R>>.call8(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any> ThisFun<out Function8<A1, A2, A3, A4, A5, A6, A7, A8, R>>.callNt8(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any> ThisFun<out Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R>>.call9(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any> ThisFun<out Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R>>.callNt9(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any, A10 : Any> ThisFun<out Function10<R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10>>.call10(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
    arg10: KRef<A10>,
): KRef.Obj<R> {
    return KRef.Obj(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any, A10 : Any> ThisFun<out Function10<R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10>>.callNt10(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
    arg10: KRef<A10>,
): KRef.Native<R> {
    return KRef.Native(R::class, CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10), this))
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any, A10 : Any, A11 : Any> ThisFun<out Function11<R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11>>.call11(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
    arg10: KRef<A10>,
    arg11: KRef<A11>,
): KRef.Obj<R> {
    return KRef.Obj(
        R::class,
        CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11), this)
    )
}

inline fun <reified R : Any, A1 : Any, A2 : Any, A3 : Any, A4 : Any, A5 : Any, A6 : Any, A7 : Any, A8 : Any, A9 : Any, A10 : Any, A11 : Any> ThisFun<out Function11<R, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11>>.callNt11(
    arg1: KRef<A1>,
    arg2: KRef<A2>,
    arg3: KRef<A3>,
    arg4: KRef<A4>,
    arg5: KRef<A5>,
    arg6: KRef<A6>,
    arg7: KRef<A7>,
    arg8: KRef<A8>,
    arg9: KRef<A9>,
    arg10: KRef<A10>,
    arg11: KRef<A11>,
): KRef.Native<R> {
    return KRef.Native(
        R::class,
        CallItself(arrayOf(arg1, arg2, arg3, arg4, arg5, arg6, arg7, arg8, arg9, arg10, arg11), this)
    )
}
