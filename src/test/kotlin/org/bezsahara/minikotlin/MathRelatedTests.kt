package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.ofType
import org.bezsahara.minikotlin.builder.declaration.static
import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.builder.opcodes.ext.dup
import org.bezsahara.minikotlin.builder.opcodes.ext.iconst_0
import org.bezsahara.minikotlin.builder.opcodes.ext.iconst_1
import org.bezsahara.minikotlin.builder.opcodes.ext.iload
import org.bezsahara.minikotlin.builder.opcodes.ext.invokestatic
import org.bezsahara.minikotlin.builder.opcodes.ext.ldc
import org.bezsahara.minikotlin.builder.opcodes.ext.newarray
import org.bezsahara.minikotlin.builder.opcodes.ext.return_
import org.bezsahara.minikotlin.builder.opcodes.ext.saload
import org.bezsahara.minikotlin.builder.opcodes.ext.sastore
import org.bezsahara.minikotlin.builder.opcodes.helpers.print
import org.bezsahara.minikotlin.builder.opcodes.helpers.printI
import org.bezsahara.minikotlin.builder.singleFunImplMiniKt
import org.bezsahara.minikotlin.lan.callMethod
import org.bezsahara.minikotlin.lan.lib.bool
import org.bezsahara.minikotlin.lan.lib.echoLine
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.lib.toStr
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.logic.greaterOrEq
import org.bezsahara.minikotlin.lan.logic.greaterThan
import org.bezsahara.minikotlin.lan.logic.lessThan
import org.bezsahara.minikotlin.lan.logic.math.asInt
import org.bezsahara.minikotlin.lan.logic.notEq
import org.bezsahara.minikotlin.lan.logic.or
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.runsMiniKt
import java.lang.reflect.Type
import kotlin.test.Test
import kotlin.test.assertEquals

object MathRelatedTests {
    interface MathTest {
        fun test(
            case: Int,
            d1: Double,
            d2: Double,
            f1: Float,
            f2: Float,
            l1: Long,
            l2: Long

        ): String
    }

    @Test
    fun `cat2 comparisons`() {
        val t1 = singleFunImplMiniKt(MathTest::class, MathTest::test) {
            val case = variableNt<Int>("case")
            val d1 = variableNt<Double>("d1")
            val d2 = variableNt<Double>("d2")
            val f1 = variableNt<Float>("f1")
            val f2 = variableNt<Float>("f2")
            val l1 = variableNt<Long>("l1")
            val l2 = variableNt<Long>("l2")

            switch(case) {
                case(0) {
                    if_(d1 greaterThan d2) {
                        thisFun.return_(str("fine"))
                    }
                }
                case(1) {
                    if_(f1 lessThan f2 or bool(false)) {
                        thisFun.return_(str("float ok"))
                    }
                }
                case(2) {
                    if_(l1 eq l2) {
                        thisFun.return_(str("long match"))
                    }
                }
                case(3) {
                    if_(d1 notEq d2) {
                        thisFun.return_(str("double mismatch"))
                    }
                }
                case(4) {
                    if_(l1 greaterOrEq l2) {
                        thisFun.return_(str("long ge"))
                    }
                }
                default {
                    thisFun.return_(str("default"))
                }
            }

            thisFun.return_(str("default"))
        }

        val ins = t1.initAndGetAsInterface<MathTest>()

        assertEquals("fine", ins.test(0, 10.0, 2.0, 0f, 0f, 0, 0))
        assertEquals("float ok", ins.test(1, 0.0, 0.0, 1.0f, 2.0f, 0, 0))
        assertEquals("long match", ins.test(2, 0.0, 0.0, 0f, 0f, 5L, 5L))
        assertEquals("double mismatch", ins.test(3, 5.5, 6.5, 0f, 0f, 0, 0))
        assertEquals("long ge", ins.test(4, 0.0, 0.0, 0f, 0f, 7L, 3L))
        assertEquals("default", ins.test(9, 0.0, 0.0, 0f, 0f, 0, 0))
    }

    interface IntegerDifferent {
        fun test()
    }

    @JvmStatic
    fun accept32(i: Int) {
        println("Accepted: $i")
    }

    @Test
    fun `integer antics`() {
        val cl = makeClass("IDTest") implements IntegerDifferent::class body {
            autoInit()

            val m = private static ofType(TypeInfo.Void) method "just"("i" to TypeInfo.Short) runsMiniKt {
                val i = variableNt<Short>("i")
                echoLine(str("i is: "), i.toStr())
                return_()
            }

            implOf(IntegerDifferent::test) runsMiniKt {
                call1(::accept32, number(3.toShort()).asInt()).exec()

                m.callMethod<Unit>(number(Int.MAX_VALUE)).exec()
                return_()
            }
        }

        val result = cl.result()

        val ins = result.initAndGetAsInterface<IntegerDifferent>()
        ins.test()
    }

    @Test
    fun `integer and short`() {
        val cl = makeClass("IDTest") implements IntegerDifferent::class body {
            autoInit()

            val m = private static ofType(TypeInfo.Void) method "just"("i" to TypeInfo.Short) runs {
                iload(0)
                printI()
                return_()
            }

            implOf(IntegerDifferent::test) runs {

                iconst_1()
                newarray(Short::class)
                dup()
                iconst_0()
                ldc(Int.MAX_VALUE)
                sastore()

                iconst_0()
                saload()
                invokestatic(ThisClass, "just", m.methodDescriptor())
                return_()
            }
        }

        val result = cl.result()

        val ins = result.initAndGetAsInterface<IntegerDifferent>()
        ins.test()
    }
}