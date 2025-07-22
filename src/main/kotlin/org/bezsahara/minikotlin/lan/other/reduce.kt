package org.bezsahara.minikotlin.lan.other

interface Some {
    fun cat()
}

fun test(l: Some) {
    val j = l::cat
    val j2 = Some::cat
}

//fun <R: Any> Function1<*, R>.reduce(): Function0<R> {
//}