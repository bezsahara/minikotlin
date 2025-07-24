@file:Suppress("UNCHECKED_CAST", "unused")

package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.pieces.FunCall
import kotlin.reflect.KFunction

inline fun <reified R: Any> MiniKotlin<*>.call(
    noinline f: Function0<R?>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        emptyArray(),
        false
    ))
}

inline fun <reified R: Any> MiniKotlin<*>.callNt(
    noinline f: Function0<R?>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        emptyArray(),
        true
    ))
}

inline fun <reified R: Any, A1> MiniKotlin<*>.call1(
    noinline f: Function1<A1, R?>,
    a1: KRef<out A1>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use()),
        false
    ))
}

inline fun <reified R: Any, A1> MiniKotlin<*>.callNt1(
    noinline f: Function1<A1, R?>,
    a1: KRef<out A1>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use()),
        true
    ))
}

fun <R: Any, A1> MiniKotlin<*>.call1(
    f: Function1<A1, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use()),
        false
    ))
}

fun <R: Any, A1> MiniKotlin<*>.callNt1(
    f: Function1<A1, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2> MiniKotlin<*>.call2(
    noinline f: Function2<A1, A2, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2> MiniKotlin<*>.callNt2(
    noinline f: Function2<A1, A2, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use()),
        true
    ))
}

fun <R: Any, A1, A2> MiniKotlin<*>.call2(
    f: Function2<A1, A2, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use()),
        false
    ))
}

fun <R: Any, A1, A2> MiniKotlin<*>.callNt2(
    f: Function2<A1, A2, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3> MiniKotlin<*>.call3(
    noinline f: Function3<A1, A2, A3, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3> MiniKotlin<*>.callNt3(
    noinline f: Function3<A1, A2, A3, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3> MiniKotlin<*>.call3(
    f: Function3<A1, A2, A3, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3> MiniKotlin<*>.callNt3(
    f: Function3<A1, A2, A3, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4> MiniKotlin<*>.call4(
    noinline f: Function4<A1, A2, A3, A4, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4> MiniKotlin<*>.callNt4(
    noinline f: Function4<A1, A2, A3, A4, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4> MiniKotlin<*>.call4(
    f: Function4<A1, A2, A3, A4, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4> MiniKotlin<*>.callNt4(
    f: Function4<A1, A2, A3, A4, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5> MiniKotlin<*>.call5(
    noinline f: Function5<A1, A2, A3, A4, A5, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5> MiniKotlin<*>.callNt5(
    noinline f: Function5<A1, A2, A3, A4, A5, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5> MiniKotlin<*>.call5(
    f: Function5<A1, A2, A3, A4, A5, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5> MiniKotlin<*>.callNt5(
    f: Function5<A1, A2, A3, A4, A5, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6> MiniKotlin<*>.call6(
    noinline f: Function6<A1, A2, A3, A4, A5, A6, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6> MiniKotlin<*>.callNt6(
    noinline f: Function6<A1, A2, A3, A4, A5, A6, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6> MiniKotlin<*>.call6(
    f: Function6<A1, A2, A3, A4, A5, A6, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6> MiniKotlin<*>.callNt6(
    f: Function6<A1, A2, A3, A4, A5, A6, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7> MiniKotlin<*>.call7(
    noinline f: Function7<A1, A2, A3, A4, A5, A6, A7, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7> MiniKotlin<*>.callNt7(
    noinline f: Function7<A1, A2, A3, A4, A5, A6, A7, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7> MiniKotlin<*>.call7(
    f: Function7<A1, A2, A3, A4, A5, A6, A7, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7> MiniKotlin<*>.callNt7(
    f: Function7<A1, A2, A3, A4, A5, A6, A7, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8> MiniKotlin<*>.call8(
    noinline f: Function8<A1, A2, A3, A4, A5, A6, A7, A8, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8> MiniKotlin<*>.callNt8(
    noinline f: Function8<A1, A2, A3, A4, A5, A6, A7, A8, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8> MiniKotlin<*>.call8(
    f: Function8<A1, A2, A3, A4, A5, A6, A7, A8, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8> MiniKotlin<*>.callNt8(
    f: Function8<A1, A2, A3, A4, A5, A6, A7, A8, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9> MiniKotlin<*>.call9(
    noinline f: Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9> MiniKotlin<*>.callNt9(
    noinline f: Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9> MiniKotlin<*>.call9(
    f: Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9> MiniKotlin<*>.callNt9(
    f: Function9<A1, A2, A3, A4, A5, A6, A7, A8, A9, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> MiniKotlin<*>.call10(
    noinline f: Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> MiniKotlin<*>.callNt10(
    noinline f: Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> MiniKotlin<*>.call10(
    f: Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10> MiniKotlin<*>.callNt10(
    f: Function10<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11> MiniKotlin<*>.call11(
    noinline f: Function11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11> MiniKotlin<*>.callNt11(
    noinline f: Function11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11> MiniKotlin<*>.call11(
    f: Function11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11> MiniKotlin<*>.callNt11(
    f: Function11<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12> MiniKotlin<*>.call12(
    noinline f: Function12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12> MiniKotlin<*>.callNt12(
    noinline f: Function12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12> MiniKotlin<*>.call12(
    f: Function12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12> MiniKotlin<*>.callNt12(
    f: Function12<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13> MiniKotlin<*>.call13(
    noinline f: Function13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13> MiniKotlin<*>.callNt13(
    noinline f: Function13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13> MiniKotlin<*>.call13(
    f: Function13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13> MiniKotlin<*>.callNt13(
    f: Function13<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14> MiniKotlin<*>.call14(
    noinline f: Function14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14> MiniKotlin<*>.callNt14(
    noinline f: Function14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14> MiniKotlin<*>.call14(
    f: Function14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14> MiniKotlin<*>.callNt14(
    f: Function14<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15> MiniKotlin<*>.call15(
    noinline f: Function15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15> MiniKotlin<*>.callNt15(
    noinline f: Function15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15> MiniKotlin<*>.call15(
    f: Function15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15> MiniKotlin<*>.callNt15(
    f: Function15<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16> MiniKotlin<*>.call16(
    noinline f: Function16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16> MiniKotlin<*>.callNt16(
    noinline f: Function16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16> MiniKotlin<*>.call16(
    f: Function16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16> MiniKotlin<*>.callNt16(
    f: Function16<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17> MiniKotlin<*>.call17(
    noinline f: Function17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17> MiniKotlin<*>.callNt17(
    noinline f: Function17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17> MiniKotlin<*>.call17(
    f: Function17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17> MiniKotlin<*>.callNt17(
    f: Function17<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18> MiniKotlin<*>.call18(
    noinline f: Function18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18> MiniKotlin<*>.callNt18(
    noinline f: Function18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18> MiniKotlin<*>.call18(
    f: Function18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18> MiniKotlin<*>.callNt18(
    f: Function18<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19> MiniKotlin<*>.call19(
    noinline f: Function19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19> MiniKotlin<*>.callNt19(
    noinline f: Function19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19> MiniKotlin<*>.call19(
    f: Function19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19> MiniKotlin<*>.callNt19(
    f: Function19<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20> MiniKotlin<*>.call20(
    noinline f: Function20<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20> MiniKotlin<*>.callNt20(
    noinline f: Function20<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20> MiniKotlin<*>.call20(
    f: Function20<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20> MiniKotlin<*>.callNt20(
    f: Function20<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21> MiniKotlin<*>.call21(
    noinline f: Function21<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21> MiniKotlin<*>.callNt21(
    noinline f: Function21<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21> MiniKotlin<*>.call21(
    f: Function21<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21> MiniKotlin<*>.callNt21(
    f: Function21<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use()),
        true
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22> MiniKotlin<*>.call22(
    noinline f: Function22<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>, a22: KRef<out A22>
): KRef.Obj<R> {
    return KRef.Obj(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use(), a22.use()),
        false
    ))
}

inline fun <reified R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22> MiniKotlin<*>.callNt22(
    noinline f: Function22<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, R?>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>, a22: KRef<out A22>
): KRef.Native<R> {
    return KRef.Native(R::class, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use(), a22.use()),
        true
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22> MiniKotlin<*>.call22(
    f: Function22<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>, a22: KRef<out A22>
): KRef.Obj<R> {
    return KRef.Obj(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use(), a22.use()),
        false
    ))
}

fun <R: Any, A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22> MiniKotlin<*>.callNt22(
    f: Function22<A1, A2, A3, A4, A5, A6, A7, A8, A9, A10, A11, A12, A13, A14, A15, A16, A17, A18, A19, A20, A21, A22, R?>,
    returnType: Class<R>,
    a1: KRef<out A1>, a2: KRef<out A2>, a3: KRef<out A3>, a4: KRef<out A4>, a5: KRef<out A5>, a6: KRef<out A6>, a7: KRef<out A7>, a8: KRef<out A8>, a9: KRef<out A9>, a10: KRef<out A10>, a11: KRef<out A11>, a12: KRef<out A12>, a13: KRef<out A13>, a14: KRef<out A14>, a15: KRef<out A15>, a16: KRef<out A16>, a17: KRef<out A17>, a18: KRef<out A18>, a19: KRef<out A19>, a20: KRef<out A20>, a21: KRef<out A21>, a22: KRef<out A22>
): KRef.Native<R> {
    return KRef.Native(returnType, FunCall(
        f as KFunction<R>,
        arrayOf(a1.use(), a2.use(), a3.use(), a4.use(), a5.use(), a6.use(), a7.use(), a8.use(), a9.use(), a10.use(), a11.use(), a12.use(), a13.use(), a14.use(), a15.use(), a16.use(), a17.use(), a18.use(), a19.use(), a20.use(), a21.use(), a22.use()),
        true
    ))
}
