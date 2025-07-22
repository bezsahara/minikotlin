package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.lan.lib.nullValue
import org.bezsahara.minikotlin.lan.lib.objectRefOf
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.logic.call
import kotlin.test.Test



interface IMain {

}

object ISecond : IMain {

}


fun pizza(): ISecond {
    return ISecond
}

object InheritanceCheck {
    @Test
    fun `variables work correctly (hopefully)`() {
        val ins: () -> Unit = implLambdaMiniKt {
            val that = variable<IMain>("kkk")
            val ss = call(::pizza)
            that setTo ss

            return_(nullValue<Any>())
        }
    }
}