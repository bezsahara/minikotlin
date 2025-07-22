package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.lan.lib.echo
import org.bezsahara.minikotlin.lan.lib.echoLine
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.logic.not
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.toVariable
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertEquals

object ConditionsTests {
    interface IfElseTest {
        fun test(flag: Boolean): Int
    }

    @Test
    fun `separate if else`() {
        val cl = makeClass("testIfElse") implements IfElseTest::class body {
            autoInit()

            implOf(IfElseTest::test) runsMiniKt {
                val flag = variableNt<Boolean>("flag")
                val kind = number(-1).toVariable()

                if_(flag) {
                    kind setTo number(10)
                }

                if_(not(flag)) {
                    kind setTo number(20)
                }

                thisFun.return_(kind)
            }
        }


        val result = cl.result()

        result.printCode()

        val ins = result.initAndGetAsInterface<IfElseTest>()

        assertEquals(20, ins.test(false))
        assertEquals(10, ins.test(true))
    }

    @Test
    fun `if else`() {
        val cl = makeClass("testIfElse") implements IfElseTest::class body {
            autoInit()

            implOf(IfElseTest::test) runsMiniKt {
                val flag = variableNt<Boolean>("flag")
                val kind = number(-1).toVariable()

                if_(flag) {
                    kind setTo number(10)
                } else_ {
                    kind setTo number(20)
                }


                thisFun.return_(kind)
            }
        }


        val result = cl.result()

        val ins = result.initAndGetAsInterface<IfElseTest>()

        assertEquals(20, ins.test(false))
        assertEquals(10, ins.test(true))

//        val cac = Class.forName("kotlin.reflect.jvm.internal.CachesKt")
//        val f = cac.declaredFields.first { it.name == "K_CLASS_CACHE" }
//        f.isAccessible = true
//        val value = f.get(null)
//
//        print(value::class.java)
    }

    @Test
    fun `if else fails`() {
        assertThrows<IllegalStateException> {
            makeClass("testIfElse") implements IfElseTest::class body {
                autoInit()

                implOf(IfElseTest::test) runsMiniKt {
                    val flag = variableNt<Boolean>("flag")
                    val kind = number(-1).toVariable()

                    val ifBlock = if_(flag) {
                        kind setTo number(10)
                    }

                    // some action
                    kind setTo number(33)
                    ifBlock.else_ {
                        kind setTo number(9999)
                    }

                    thisFun.return_(kind)
                }
            }
        }.also {
            assertEquals("You cannot add else_ now", it.message)
        }
    }
}