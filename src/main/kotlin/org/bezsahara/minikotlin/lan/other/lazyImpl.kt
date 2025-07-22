package org.bezsahara.minikotlin.lan.other

import kotlin.reflect.KProperty

@Suppress("ClassName")
object UNINITIALIZED_VALUE
//
class SynchronizedLazyCustom<out T, F: Function0<T>>(initializer: F) {
    private var initializer: (F)? = initializer

    @Volatile
    private var _value: Any? = UNINITIALIZED_VALUE

    // final field to ensure safe publication of 'SynchronizedLazyImpl' itself through
    // var lazy = lazy() {}

    inline operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        return value
    }

    val value: T
        get() {
            val _v1 = _value
            if (_v1 !== UNINITIALIZED_VALUE) {
                @Suppress("UNCHECKED_CAST")
                return _v1 as T
            }

            return synchronized(this) {
                val _v2 = _value
                if (_v2 !== UNINITIALIZED_VALUE) {
                    @Suppress("UNCHECKED_CAST") (_v2 as T)
                } else {
                    val typedValue = initializer!!()
                    _value = typedValue
                    initializer = null
                    typedValue
                }
            }
        }

    override fun toString(): String = if (_value !== UNINITIALIZED_VALUE) value.toString() else "Lazy value not initialized yet."
}