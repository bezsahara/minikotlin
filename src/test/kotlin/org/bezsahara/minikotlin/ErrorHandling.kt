package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.ErrorHandling.CErr
import org.bezsahara.minikotlin.ErrorHandling.WRErr
import org.bezsahara.minikotlin.builder.singleFunImplMiniKt
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.lib.bool
import org.bezsahara.minikotlin.lan.lib.joinStringsOf
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.logic.call
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.logic.greaterThan
import org.bezsahara.minikotlin.lan.logic.math.plus
import org.bezsahara.minikotlin.lan.logic.notEq
import org.bezsahara.minikotlin.lan.toVariable
import org.junit.jupiter.api.assertThrows
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

object ErrorHandling {
    interface ErrorTest {
        fun test(): Boolean
    }


    @Test
    fun `can catch itself`() {
        val result = singleFunImplMiniKt(ErrorTest::class, ErrorTest::test) {
            try_ {
                throw_(call(::Error))
            }.catch<Error> {
                return_(bool(true))
            }
            return_(bool(false))
        }

        result.printCode()
        result.saveToTestFolderIfAny()
        val ins = result.initAndGetAsInterface<ErrorTest>()


        assertTrue(ins.test())
    }

    interface ErrorTestAdvanced {
        fun test(obj: Throwable?, case: Int): String
    }

    class CErr : RuntimeException()


    class WRErr(override val message: String?) : RuntimeException() {
    }

    class Absolute : RuntimeException()

    @Test
    fun `throws in different scopes`() {
        val result = singleFunImplMiniKt(ErrorTestAdvanced::class, ErrorTestAdvanced::test) {
            val obj = variable<Throwable>("obj")
            val case = variableNt<Int>("case")

            val counter = number(0).toVariable("counter")
            if_(case notEq number(2)) {
                try_ {

                    switch(case) {
                        case(0) {
                            try_ {
                                throw_(call(::CErr))
                            }.catch<WRErr> {
                                return_(str("CErr was thrown and caught"))
                            }
                        }

                        case(1) {
                            try_ {
                                try_ {
                                    try_ {
                                        throw_(call(::Throwable))
                                    }.catchAny {
                                        throw_(call1(::WRErr, str("Mark1")))
                                    }
                                }.catch<CErr> {
                                    return_(str("CErr was caught"))
                                }
                            }.catch<WRErr> {
                                return_(joinStringsOf(str("WRErr was caught "), propertyGet(WRErr::message, it)))
                            }
                        }
                        default {}
                    }

                    throw_(obj)
                }.catch<CErr> {
                    counter setTo counter + number(1)
                }
            }

            if_(counter greaterThan number(0)) {
                return_(str("+"))
            }
            return_(str("-"))
        }

        result.saveToTestFolderIfAny()
        val ins = result.initAndGetAsInterface<ErrorTestAdvanced>()


        assertEquals("-", ins.test(null, 2))
        assertEquals("+", ins.test(CErr(), 3))
        assertThrows<Absolute> { ins.test(Absolute(), 3) }
        assertEquals("WRErr was caught Mark1", ins.test(null, 1))
    }
}