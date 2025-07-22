package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.ReusableRefObj


inline fun <reified T: Function<*>> MiniKotlin<*>.kotlinLambda(block: T): ReusableRefObj<T> {
    return ReusableRefObj(T::class.java, OuterKey(kbClass, block, block.javaClass.interfaces.firstOrNull { it.name.startsWith("kotlin.jvm.functions.Function") }!!))
}

context(mk: MiniKotlin<*>)
inline fun <T: Function2<A1, A2, R>, A1: Any, A2: Any, reified R: Any> KRef.Obj<T>.call(a1: KRef.Obj<A1>, a2: KRef.Obj<A2>): KRef.Obj<R> {
    return mk.call3(Function2<A1, A2, R>::invoke, this, a1, a2)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function1<A1, R>, A1: Any, reified R: Any> KRef.Obj<T>.call(a1: KRef.Obj<A1>): KRef.Obj<R> {
    return mk.call2(Function1<A1, R>::invoke, this, a1)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function0<R>, reified R: Any> KRef.Obj<T>.call(): KRef.Obj<R> {
    return mk.call1(Function0<R>::invoke, this)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function3<A1, A2, A3, R>, A1: Any, A2: Any, A3: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>
): KRef.Obj<R> {
    return mk.call4(Function3<A1, A2, A3, R>::invoke, this, a1, a2, a3)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function4<A1, A2, A3, A4, R>, A1: Any, A2: Any, A3: Any, A4: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>
): KRef.Obj<R> {
    return mk.call5(Function4<A1, A2, A3, A4, R>::invoke, this, a1, a2, a3, a4)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function5<A1, A2, A3, A4, A5, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>
): KRef.Obj<R> {
    return mk.call6(Function5<A1, A2, A3, A4, A5, R>::invoke, this, a1, a2, a3, a4, a5)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function6<A1, A2, A3, A4, A5, A6, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, A6: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>, a6: KRef.Obj<A6>
): KRef.Obj<R> {
    return mk.call7(Function6<A1, A2, A3, A4, A5, A6, R>::invoke, this, a1, a2, a3, a4, a5, a6)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function7<A1, A2, A3, A4, A5, A6, A7, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, A6: Any, A7: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>, a6: KRef.Obj<A6>, a7: KRef.Obj<A7>
): KRef.Obj<R> {
    return mk.call8(Function7<A1, A2, A3, A4, A5, A6, A7, R>::invoke, this, a1, a2, a3, a4, a5, a6, a7)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function8<A1, A2, A3, A4, A5, A6, A7, A8, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, A6: Any, A7: Any, A8: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>, a6: KRef.Obj<A6>, a7: KRef.Obj<A7>, a8: KRef.Obj<A8>
): KRef.Obj<R> {
    return mk.call9(Function8<A1, A2, A3, A4, A5, A6, A7, A8, R>::invoke, this, a1, a2, a3, a4, a5, a6, a7, a8)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, A6: Any, A7: Any, A8: Any, A9: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>, a6: KRef.Obj<A6>, a7: KRef.Obj<A7>, a8: KRef.Obj<A8>, a9: KRef.Obj<A9>
): KRef.Obj<R> {
    return mk.call10(Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R>::invoke, this, a1, a2, a3, a4, a5, a6, a7, a8, a9)
}

context(mk: MiniKotlin<*>)
inline fun <T: Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R>, A1: Any, A2: Any, A3: Any, A4: Any, A5: Any, A6: Any, A7: Any, A8: Any, A9: Any, A10: Any, reified R: Any> KRef.Obj<T>.call(
    a1: KRef.Obj<A1>, a2: KRef.Obj<A2>, a3: KRef.Obj<A3>, a4: KRef.Obj<A4>, a5: KRef.Obj<A5>, a6: KRef.Obj<A6>, a7: KRef.Obj<A7>, a8: KRef.Obj<A8>, a9: KRef.Obj<A9>, a10: KRef.Obj<A10>
): KRef.Obj<R> {
    return mk.call11(Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R>::invoke, this, a1, a2, a3, a4, a5, a6, a7, a8, a9, a10)
}