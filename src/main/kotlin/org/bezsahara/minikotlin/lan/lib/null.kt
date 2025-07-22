package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.lan.KRef

inline fun <reified T: Any> nullValue(): KRef.Null<T> {
    return KRef.Null(T::class)
}

