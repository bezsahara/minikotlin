package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.lan.lib.forEach
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.logic.call
import org.bezsahara.minikotlin.lan.logic.callNt2
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.toVariable

fun main() {
    val transform: (Iterator<Any>) -> ArrayList<Any> = implLambdaMiniKt {
        val iter = variable<Iterator<Any>>("arg0")


        val arrayList = call<ArrayList<Any>>(::ArrayList).toVariable("arrayList")

        iter.forEach {
            callNt2(ArrayList<Any>::add, arrayList, it).exec()
        }

        thisFun.return_(arrayList)
    }


    println("S${transform(listOf(1,2,3,4,5).iterator())} ")
}