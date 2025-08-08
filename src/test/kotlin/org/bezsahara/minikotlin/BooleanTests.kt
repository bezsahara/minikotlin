package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.lan.lib.bool
import org.bezsahara.minikotlin.lan.lib.boxNumber
import org.bezsahara.minikotlin.lan.lib.castTo
import org.bezsahara.minikotlin.lan.lib.instanceOf
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.logic.and
import org.bezsahara.minikotlin.lan.logic.callNt1
import org.bezsahara.minikotlin.lan.logic.end
import org.bezsahara.minikotlin.lan.logic.math.asInt
import org.bezsahara.minikotlin.lan.logic.not
import org.bezsahara.minikotlin.lan.logic.or
import org.bezsahara.minikotlin.lan.other.AbstractInstanceObject
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.toVariable
import org.junit.jupiter.api.Assertions.assertFalse
//import org.junit.jupiter.api.Assertions.assertFalse
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

interface BoolTest {
    fun test(
        opId: Int,
        a: Boolean,
        b: Boolean,
        c: Boolean,
        d: Boolean,
        e: Boolean,
        f: Boolean
    ): Boolean

    companion object : AbstractInstanceObject<BoolTest>()
}

object BooleanTests {
    @Test
    fun `boolean operations okay`() {
        val cl = makeClass("boolTest") implements BoolTest::class body {
            this.autoInitAndReturn()

            implOf(BoolTest()::test) runsMiniKt {
                val opId = variableNt<Int>("opId")
                val a = variableNt<Boolean>("a")
                val b = variableNt<Boolean>("b")
                val c = variableNt<Boolean>("c")
                val d = variableNt<Boolean>("d")
                val e = variableNt<Boolean>("e")
                val f = variableNt<Boolean>("f")

                not(bool(true)).toVariable("notbool")

                val p= (a.not()).toVariable("ss")
//                echoLine(p.toStr())
                switch(opId) {
                    case(0) { // a && b && c
                        if_(a and b and c) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(1) { // a || b || c
                        if_(a or b or c) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(2) { // a && b || c && d
                        if_(a and b or c and d) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(3) { // a || b && c || d
                        if_(a or b and c or d) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(4) { // a && b && c && d && e && f
                        if_(a and b and c and d and e and f) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(5) { // !(a && b)
                        if_(not(a and b)) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(6) { // !(a) || b
                        if_(a.not() or b) { // TODO make not() accept labels
                            thisFun.return_(bool(true))
                        }
                    }
                    case(7) { // (a && b) || (c && d)
                        if_((a and b).end() or (c and d).end()) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(8) { // (a || b) && (c || d)
                        if_((a or b).end() and (c or d).end()) {
                            thisFun.return_(bool(true))
                        }
                    }
                    case(9) { // not((a || b) && c)
                        if_(((a or b).end() and c).end().not()) {
                            thisFun.return_(bool(true))
                        }
                    }
                    default {

                    }
                }

                thisFun.return_(bool(false))
            }
        }

        val result = cl.result()
        result.saveToTestFolderIfAny()
        val ins = result.initAndGetAsInterface<BoolTest>()

        // case 0: a && b && c
        assertTrue(ins.test(0, true, true, true, false, false, false))
        assertFalse(ins.test(0, true, false, true, false, false, false))

        // case 1: a || b || c
        assertTrue(ins.test(1, false, false, true, false, false, false))
        assertFalse(ins.test(1, false, false, false, false, false, false))

        // case 2: a && b || c && d
        assertTrue(ins.test(2, true, true, false, true, false, false)) // a && b is true
        assertTrue(ins.test(2, false, false, true, true, false, false)) // c && d is true
        assertFalse(ins.test(2, false, true, true, false, false, false)) // both false

        // case 3: a || b && c || d
        assertTrue(ins.test(3, false, true, true, false, false, false)) // b && c
        assertTrue(ins.test(3, false, false, false, true, false, false)) // d
        assertFalse(ins.test(3, false, false, false, false, false, false)) // all false

        // case 4: all true
        assertTrue(ins.test(4, true, true, true, true, true, true))
        assertFalse(ins.test(4, true, true, true, true, false, true))

        // case 5: not(a && b)
        assertTrue(ins.test(5, false, false, false, false, false, false))
        assertFalse(ins.test(5, true, true, false, false, false, false))

        // case 6: not(a) || b
        assertTrue(ins.test(6, true, true, false, false, false, false))
        assertTrue(ins.test(6, false, false, false, false, false, false))
        assertFalse(ins.test(6, true, false, false, false, false, false))

        // case 7: (a && b) || (c && d)
        assertTrue(ins.test(7, true, true, false, true, false, false))
        assertTrue(ins.test(7, false, false, true, true, false, false))
        assertFalse(ins.test(7, true, false, true, false, false, false))

        // case 8: (a || b) && (c || d)
        assertTrue(ins.test(8, true, false, false, true, false, false))
        assertFalse(ins.test(8, false, false, true, false, false, false))

        // case 9: not((a || b) && c)
        assertTrue(ins.test(9, false, false, false, false, false, false))
        assertTrue(ins.test(9, false, true, false, false, false, false))
        assertFalse(ins.test(9, true, true, true, false, false, false))
    }

    class JustKey {
        fun test(): Int {
            return 900
        }
    }

    @Test
    fun `checks of instances`() {
        val ins: (Any) -> Int = implLambdaMiniKt {
            val obj = variable<Any>("arg0")

            if_(obj.instanceOf(JustKey::class)) {
                val jk = obj.castTo<JustKey>()
                thisFun.return_(boxNumber(callNt1(JustKey::test, jk)))
            }

            thisFun.return_(boxNumber(obj.instanceOf(String::class).asInt()))
        }

        assertEquals(1, ins(""))
        assertEquals(0, ins(Any()))
        assertEquals(900, ins(JustKey()))
    }
}