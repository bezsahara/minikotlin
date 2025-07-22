package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.lan.*
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.logic.callNt1
import org.bezsahara.minikotlin.lan.logic.lessThan
import org.bezsahara.minikotlin.lan.logic.math.plus
import org.bezsahara.minikotlin.lan.logic.whileDo

@JvmName("forEachArray")
context(mk: MiniKotlinAny)
inline fun <reified T: Any> KRef.Obj<Array<T>>.forEach(block: WhileLoop.Scope.(KRef.Obj<T>) -> Unit) {
    mk.apply {
        val arrSize = size().toVariable()
        val counter = number(0).toVariable()

        whileDo(counter lessThan arrSize) {
            block(this@forEach[counter])
            counter setTo counter + number(1)
        }
    }
}

context(mk: MiniKotlin<out Any>)
inline fun <reified T : Any> KRef.Obj<out Iterator<T>>.forEach(block: WhileLoop.Scope.(KRef.Obj<T>) -> Unit ) {
    mk.apply {
        whileDo(callNt1(Iterator<T>::hasNext, this@forEach)) {
            val next = call1(Iterator<T>::next, this@forEach)
            block(next)
        }
    }
}