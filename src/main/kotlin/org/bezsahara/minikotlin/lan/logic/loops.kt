package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.WhileLoop


inline fun MiniKotlin<*>.whileDo(condition: KRef.Native<Boolean>, block: WhileLoop.Scope.() -> Unit) {
    WhileLoop(this, condition, true).apply {
        Scope().block()
        end()
    }
}

inline fun MiniKotlin<*>.doWhile(condition: KRef.Native<Boolean>, block: WhileLoop.Scope.() -> Unit) {
    WhileLoop(this, condition, false).apply {
        Scope().block()
        end()
    }
}