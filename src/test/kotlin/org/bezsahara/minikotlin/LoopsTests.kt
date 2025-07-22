package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.builder.singleFunImplMiniKt
import org.bezsahara.minikotlin.lan.lib.forEach
import org.bezsahara.minikotlin.lan.lib.get
import org.bezsahara.minikotlin.lan.lib.miniObjectArray
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.lib.unbox
import org.bezsahara.minikotlin.lan.lib.unboxNumber
import org.bezsahara.minikotlin.lan.logic.*
import org.bezsahara.minikotlin.lan.logic.math.minus
import org.bezsahara.minikotlin.lan.logic.math.plus
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.toVariable
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals


object LoopsTests {
    @Test
    fun `while loop`() {
        //INVOKEINTERFACE java/util/List.size ()I (itf)
        val lambda: (Int) -> String = implLambdaMiniKt {
            val it = unboxNumber(variable<Int>("arg0")).toVariable("itUnboxes")
            val stuff = immutableRefTo(listOf("cat", "dog", "parrot", "human", "something else"))
            val counter = number(0).toVariable("counter")
            val s1 = propertyGetNt(List<String>::size, stuff)
            whileDo(counter lessThan s1) {
                if_(counter eq it) {
                    thisFun.return_(call2(List<String>::get, stuff, counter))
                }
                counter setTo counter + number(1)
            }
            throw_(call1(::IllegalStateException, str("No other available")))
            thisFun.return_(str("-"))
        }

        assertEquals("cat", lambda(0))
        assertEquals("parrot", lambda(2))
        assertEquals("human", lambda(3))
        assertEquals("something else", lambda(4))
        assertThrows<IllegalStateException> { lambda(5) }
    }

    @Test
    fun `while loop in if`() {
        val lambda: (Int) -> String = implLambdaMiniKt {
            val it = variable<Int>("arg0").unbox().toVariable("itUnboxes")

            if_(it greaterThan number(0)) {
                if_(it eq number(22)) {
                    whileDo(it notEq number(0)) {
                        it setTo it - number(1)
                    }
                    thisFun.return_(str("+"))
                } else_ {
                    thisFun.return_(str("?"))
                }
            }

            thisFun.return_(str("-"))
        }

        assertEquals("+", lambda(22))
        assertEquals("-", lambda(-123))
        assertEquals("?", lambda(2))
    }

    interface IterLoop {
        fun test(iterator: Iterator<Any>): ArrayList<Any>
    }

    @Test
    fun `loop in lambda`() {
        val transform = implLambdaMiniKt<(Iterator<Any>) -> ArrayList<Any>> {
            val iter = variable<Iterator<Any>>("arg0")            // 1 read the single argument
            val out  = call<ArrayList<Any>>(::ArrayList)           // 2️ new ArrayList<Any>()
                .toVariable("result")

            iter.forEach {
                callNt2(ArrayList<Any>::add, out, it).exec()       // 3 out.add(it)
            }

            thisFun.return_(out)                                   // 4 return out
        }

    }

    @Test
    fun `for each loop`() {
        val transformR = singleFunImplMiniKt(IterLoop::class, IterLoop::test) {
            val iter = variable<Iterator<Any>>("iterator")


            val arrayList = call<ArrayList<Any>>(::ArrayList).toVariable("arrayList")

            iter.forEach {
                callNt2(ArrayList<Any>::add, arrayList, it).exec()
            }

            thisFun.return_(arrayList)
        }
        transformR.saveToTestFolderIfAny()
        val transform = transformR.initAndGetAsInterface<IterLoop>()

        assertEquals<Any>(arrayListOf(1,2,3,4,5), transform.test(listOf(1,2,3,4,5).iterator()))
        assertEquals<Any>(arrayListOf(1,"2",3,4,"5"), transform.test(listOf(1,"2",3,4,"5").iterator()))
        assertNotEquals<Any>(arrayListOf("""1,"2",3,4,"5""""), transform.test(listOf(1,"2",3,4,"5").iterator()))
    }
}