package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.final
import org.bezsahara.minikotlin.builder.declaration.ofType
import org.bezsahara.minikotlin.builder.declaration.static
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.builder.singleFunImplMiniKt
import org.bezsahara.minikotlin.lan.callMethod
import org.bezsahara.minikotlin.lan.callMethodNt
import org.bezsahara.minikotlin.lan.getField
import org.bezsahara.minikotlin.lan.lib.call1
import org.bezsahara.minikotlin.lan.lib.echoLine
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.lib.toStr
import org.bezsahara.minikotlin.lan.logic.call2
import org.bezsahara.minikotlin.lan.logic.callNt2
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.setField
import kotlin.test.Test

object ItselfInteraction {
    interface ThisTests {
        fun meet(): String

        fun test(t: Int)
    }

    @Test
    fun `field antics`() {
        val cl = makeClass("thisTestOfFields") implements ThisTests::class body {
            autoInit()

            val field = private final static ofType(TypeInfo.String) field "someField"

            static() runsMiniKt {
                field.setField(str("Hi"))
                return_()
            }

            val m0 = implOf(ThisTests::meet) runsMiniKt {
                return_(str("hello90"))
            }

            val m = private static ofType(TypeInfo.Int) method "call"("p1" to TypeInfo.String) runsMiniKt {
                val p1 = variable<String>("p1")


                if_(callNt2(String::equals, p1, str("hi"))) {
//                    m0.callMethodNt()
                    return_(number(0))
                }
                return_(number(1))
            }

            implOf(ThisTests::test) runsMiniKt {
                val p = variableNt<Int>("t")

                echoLine(str("Filed = "), field.getField<String>())
//                field.setField(str("Should error"))

                if_(p eq number(0)) {
                    val p = m.callMethodNt<Int>(str("1"))
                    echoLine(p.toStr())
                }
                return_()
            }
        }
        val result = cl.result()

        val ins = result.initAndGetAsInterface<ThisTests>()
        ins.test(1)
    }

    @Test
    fun `call to other member method`() {
        val cl = makeClass("thisTest") implements ThisTests::class body {
            autoInit()

            val m0 = implOf(ThisTests::meet) runsMiniKt {
                return_(str("hello90"))
            }

            val m = private static ofType(TypeInfo.Int) method "call"("p1" to TypeInfo.String) runsMiniKt {
                val p1 = variable<String>("p1")


                if_(callNt2(String::equals, p1, str("hi"))) {
//                    m0.callMethodNt()
                    return_(number(0))
                }
                return_(number(1))
            }

            implOf(ThisTests::test) runsMiniKt {
                val p = variableNt<Int>("t")
                if_(p eq number(0)) {
                    val p = m.callMethodNt<Int>(str(""))
                    echoLine(p.toStr())
                }
                return_()
            }
        }
        val result = cl.result()

        val ins = result.initAndGetAsInterface<ThisTests>()

        ins.test(1)

        ins.test(0)
    }
}