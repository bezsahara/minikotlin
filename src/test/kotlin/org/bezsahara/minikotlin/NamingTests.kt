package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.lan.lib.*
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.toVariable
import kotlin.test.Test

object NamingTests {

    interface NT {
        fun test()
    }

    @JvmStatic
    fun accept(s: Any) {}

    @Test//implements NT::class
    fun `debug names`() {
        val cl = makeClass("aa234324") implements NT::class body {
            autoInit()

            implOf(NT::test) runsMiniKt {
                val absolute = str("general").toVariable("absolute")
                if_(bool(true)) {
                    if_(bool(true)) {
                        val k = str("mid").toVariable("middle")
                        if_(bool(true)) {
                            call1(::StringBuilder, absolute).toVariable("sb")
                        } else_ {
                            number(2.0).toVariable("dub")
                        }
                    }

                    val a = number(0).toVariable("hello")
                    echoLine(a.toStr())
                    return_()
                }
                val a = boxNumber(21).toVariable("goodbye")

                echoLine(a.toStr())
                return_()
            }
        }
        val r = cl.result()
        // To test it need to save it first
        // r.saveToTestFolderIfAny()
        r.initAndGetAsInterface<NT>().test()
    }
}