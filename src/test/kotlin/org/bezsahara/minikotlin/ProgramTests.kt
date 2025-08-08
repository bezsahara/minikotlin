package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.ofType
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.declaration.static
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.builder.opcodes.ext.dup
import org.bezsahara.minikotlin.builder.opcodes.ext.iadd
import org.bezsahara.minikotlin.builder.opcodes.ext.iconst_1
import org.bezsahara.minikotlin.builder.opcodes.ext.iconst_2
import org.bezsahara.minikotlin.builder.opcodes.ext.if_icmpge
import org.bezsahara.minikotlin.builder.opcodes.ext.iload
import org.bezsahara.minikotlin.builder.opcodes.ext.invokestatic
import org.bezsahara.minikotlin.builder.opcodes.ext.ireturn
import org.bezsahara.minikotlin.builder.opcodes.ext.isub
import org.bezsahara.minikotlin.builder.opcodes.ext.labelPoint
import org.bezsahara.minikotlin.lan.lib.get
import org.bezsahara.minikotlin.lan.lib.miniIntArray
import org.bezsahara.minikotlin.lan.lib.miniIntArrayOf
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.set
import org.bezsahara.minikotlin.lan.lib.size
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.logic.lessOrEq
import org.bezsahara.minikotlin.lan.logic.lessThan
import org.bezsahara.minikotlin.lan.logic.math.minus
import org.bezsahara.minikotlin.lan.logic.math.plus
import org.bezsahara.minikotlin.lan.logic.whileDo
import org.bezsahara.minikotlin.lan.other.AbstractInstanceObject
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.toVariable
import kotlin.test.Test
import kotlin.test.assertContentEquals
import kotlin.test.assertEquals

interface FibCustom {
    fun action(n: Int): Int
}

private fun fib(n: Int): Int {
    return if (n < 2) n else fib(n - 1) + fib(n - 2)
}

interface FibTest {
    fun fib(n: Int): IntArray

    companion object : AbstractInstanceObject<FibTest>()
}
//def fib_list(n):
//    if n <= 0:
//        return []
//    elif n == 1:
//        return [0]
//    fibs = [0, 1]
//    for _ in range(2, n):
//        fibs.append(fibs[-1] + fibs[-2])
//    return fibs
object ProgramTests {
    @Test
    fun `fibonacci works`() {
        val cl = makeClass("fibTest") implements FibTest::class body {
            this.autoInitAndReturn()

            implOf(FibTest()::fib) runsMiniKt {
                val n = variableNt<Int>("n") // a parameter of the function

                switch {
                    case(n lessOrEq number(0)) {
                        thisFun.return_(miniIntArray(number(0)))
                    }
                    case(n eq number(1)) {
                        thisFun.return_(miniIntArrayOf(number(0)))
                    }
                }

                val fibs = miniIntArray(n).toVariable("fibs")
                fibs[number(0)] = number(0)
                fibs[number(1)] = number(1)
                val counter = number(2).toVariable("counter")

                whileDo(counter lessThan fibs.size()) {
                    fibs[counter] = fibs[counter - number(1)] + fibs[counter - number(2)]
                    counter setTo counter + number(1)
                }

                thisFun.return_(fibs)
            }
        }
        val ins = cl.result().initAndGetAsInterface<FibTest>()

        assertContentEquals(intArrayOf(), ins.fib(0))
        assertContentEquals(intArrayOf(0), ins.fib(1))
        assertContentEquals(intArrayOf(0, 1), ins.fib(2))
        assertContentEquals(intArrayOf(0, 1, 1), ins.fib(3))
        assertContentEquals(intArrayOf(0, 1, 1, 2, 3, 5, 8, 13), ins.fib(8))
        assertContentEquals(intArrayOf(0, 1, 1, 2, 3, 5, 8, 13, 21, 34), ins.fib(10))
    }

    /**
     * Following class is the same as:
     * ``` java
     * import org.bezsahara.kbytes.FibCustom;
     *
     * public final class someFib implements FibCustom {
     *     private static final int fib(int var0) {
     *         return var0 < 2 ? var0 : fib(var0 - 1) + fib(var0 - 2);
     *     }
     *
     *     public final int action(int var1) {
     *         return fib(var1);
     *     }
     * }
     * ```
     */
    @Test
    fun `fibonacci in bytecode`() {
        val k = makeClass("someFib", ClassProperties.Default) implements FibCustom::class implements FibTest::class body {
            this.autoInitAndReturn()

            private static final ofType(Int::class) method "fibB"("n" to TypeInfo.Kt(Int::class)) runs {
                val lessThanTwoLabel = label("lessThanTwo")
                iload(0, "n")
                dup()
                iconst_2()
                if_icmpge(lessThanTwoLabel)
                ireturn()

                labelPoint(lessThanTwoLabel)
                iconst_1()
                isub()
                invokestatic(ThisClass, "fibB", args(Int::class) returns Int::class)

                iload(0, "n")
                iconst_2()
                isub()
                invokestatic(ThisClass, "fibB", args(Int::class) returns Int::class)

                iadd()
                ireturn()
            }

            implOf(FibTest()::fib) runsMiniKt {
                val n = variableNt<Int>("n") // a parameter of the function

                switch {
                    case(n lessOrEq number(0)) {
                        thisFun.return_(miniIntArray(number(0)))
                    }
                    case(n eq number(1)) {
                        thisFun.return_(miniIntArrayOf(number(0)))
                    }
                }

                val fibs = miniIntArray(n).toVariable("fibs")
                fibs[number(0)] = number(0)
                fibs[number(1)] = number(1)
                val counter = number(2).toVariable("counter")

                whileDo(counter lessThan fibs.size()) {
                    fibs[counter] = fibs[counter - number(1)] + fibs[counter - number(2)]
                    counter setTo counter + number(1)
                }

                thisFun.return_(fibs)
            }

            implOf(FibCustom::action) runs {
                iload(variable("n"))
                invokestatic(ThisClass, "fibB", args(Int::class) returns Int::class)

                ireturn()
            }
        }
        val r = k.result()
        val ins = r.initAndGetAsInterface<FibCustom>()
        assertEquals(fib(10), ins.action(10))
        assertEquals(fib(13), ins.action(13))
        assertEquals(fib(16), ins.action(16))
        assertEquals(fib(21), ins.action(21))
    }
}
