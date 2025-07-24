package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.ofType
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.declaration.static
import org.bezsahara.minikotlin.builder.declaration.typeInfo
import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.builder.makeClass
import org.bezsahara.minikotlin.builder.opcodes.ext.areturn
import org.bezsahara.minikotlin.builder.opcodes.ext.areturn_null
import org.bezsahara.minikotlin.builder.opcodes.ext.athrow
import org.bezsahara.minikotlin.builder.opcodes.ext.dup
import org.bezsahara.minikotlin.builder.opcodes.ext.invokespecial
import org.bezsahara.minikotlin.builder.opcodes.ext.invokestatic
import org.bezsahara.minikotlin.builder.opcodes.ext.ldc
import org.bezsahara.minikotlin.builder.opcodes.ext.new
import org.bezsahara.minikotlin.compiler.asm.mapA
import org.bezsahara.minikotlin.lan.lib.bool
import org.bezsahara.minikotlin.lan.lib.boxNumber
import org.bezsahara.minikotlin.lan.lib.call1
import org.bezsahara.minikotlin.lan.lib.echoLine
import org.bezsahara.minikotlin.lan.lib.forEach
import org.bezsahara.minikotlin.lan.lib.get
import org.bezsahara.minikotlin.lan.lib.joinStringsOf
import org.bezsahara.minikotlin.lan.lib.miniIntArray
import org.bezsahara.minikotlin.lan.lib.miniIntArrayOf
import org.bezsahara.minikotlin.lan.lib.miniObjectArray
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.set
import org.bezsahara.minikotlin.lan.lib.size
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.lib.toStr
import org.bezsahara.minikotlin.lan.lib.toStrUnsafe
import org.bezsahara.minikotlin.lan.logic.call
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.logic.call2
import org.bezsahara.minikotlin.lan.logic.call3
import org.bezsahara.minikotlin.lan.logic.callNt1
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.logic.immutableRefTo
import org.bezsahara.minikotlin.lan.logic.isNotNull
import org.bezsahara.minikotlin.lan.logic.kotlinLambda
import org.bezsahara.minikotlin.lan.logic.lessOrEq
import org.bezsahara.minikotlin.lan.logic.lessThan
import org.bezsahara.minikotlin.lan.logic.math.div
import org.bezsahara.minikotlin.lan.logic.math.minus
import org.bezsahara.minikotlin.lan.logic.math.plus
import org.bezsahara.minikotlin.lan.logic.math.times
import org.bezsahara.minikotlin.lan.logic.whileDo
import org.bezsahara.minikotlin.lan.other.AbstractInstanceObject
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.runsMiniKt
import org.bezsahara.minikotlin.lan.toVariable
import org.junit.jupiter.api.RepeatedTest
import java.lang.StringBuilder
//import org.junit.jupiter.api.RepeatedTest
//import org.junit.jupiter.api.assertThrows
//import org.junit.jupiter.api.parallel.Execution
//import org.junit.jupiter.api.parallel.ExecutionMode
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.concurrent.thread
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

interface Some {
    fun test()

    fun fib(n: Int): IntArray

    fun awtWindow(label: String?)

    fun other(): Any

    companion object : AbstractInstanceObject<Some>()
}

object Sys {
    @JvmStatic
    fun test(ch: java.lang.Object) {
        println("S++++++++++++++++ - ${ch.hashCode()}")
    }
}

object GeneralTest {
    var op = 500

    fun createClass() {
        val function: () -> Unit = implLambdaMiniKt {
            echoLine(str("MiniKotlin!"))
        }
    }

//    @org.junit.jupiter.api.parallel.Execution(ExecutionMode.CONCURRENT, reason = "Test class isolation")
//    @RepeatedTest(10) // TODO create a different test to check out thread safety between instances of KBClass
//    @org.testng.annotations.Test(threadPoolSize = 3, invocationCount = 9)
//    @RepeatedTest(10)
    @Test
    fun `general test`() {
        op--

        println("o2psd: $op")
        val cl = makeClass(
            "SomeImplGT$op",
            ClassProperties.Default
        ) implements Some::class body {
            autoInit()

            public static ofType(TypeInfo.Void) method "ifelsenaming" runsMiniKt {
                if_(bool(true)) {
                    number(3).toVariable("nativeN")
                } else_ {
                    boxNumber(3).toVariable("notNative")
                }
                return_()
            }


            implOf(Some::other) runs {
                ldc("Hi")
                invokestatic(TypeInfo.of<Sys>(), "test", args(TypeInfo.Object))

                ldc("Hio")
                areturn()
            }

            public static ofType(TypeInfo.Object) method "willErr" runs {
                val owner = typeInfo<Error>()

                new(owner)
                dup()
                ldc("Error from bytecode")
                invokespecial(owner, "<init>", args(String::class) returns TypeInfo.Void)
                athrow()

                areturn_null()
            }

            implOf(Some()::test) runsMiniKt {
                return_()
            }

            //    val frame = JFrame("MiniKotlin Window")
            //    frame.setSize(400, 300)
            //    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
            //
            //    if (label != null) { frame.add(JLabel(label)) }
            //
            //    frame.isVisible = true

            implOf(Some()::awtWindow) runsMiniKt {
                val labelText = variable<String>("label") // passed as an argument

                val frame = variable<JFrame>("frame") setTo call1(::JFrame, str("MiniKotlin Window"))
                call3(JFrame::setSize, frame, number(400), number(300)).exec()

                call2(JFrame::setDefaultCloseOperation, frame, number(JFrame.EXIT_ON_CLOSE)).exec()
                if_(labelText.isNotNull()) {
                    call2(JFrame::add, frame, call1(::JLabel, labelText)).exec()
                }

                call2(JFrame::setVisible, frame, bool(true)).exec()
                return_()
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

            implOf(Some()::fib) runsMiniKt {
                val argN = variableNt<Int>("n") // from function arguments, already initialised

                val ops = callNt1(Thread::threadId, call(Thread::currentThread))

                if_(argN eq number(100)) {
                    // TODO there is something seriously wrong with naming in decompiled .class files
                    echoLine(str("-TTT---:   "), ops.toStr())
                    echoLine(str("Printed"))

                    if_(argN / number(2) * number(3) eq number(150)) {
                        echoLine(str("150!!!"))
                    } else_ {
                        val boxedInt = boxNumber(30).toVariable("ratherThen")
                        echoLine(str("Not 150("), boxedInt.toStrUnsafe())
                    }


                    val boxedInt = boxNumber(30).toVariable("boxedInt22")
                    echoLine(str("Int: "), boxedInt.toStr())

                    thisFun.return_(miniIntArray(number(0)))
                }

                // Allows type safe recursion if used with implOf
                thisFun.call1(number(100))
                    .exec() // exec is used to add this expression to actions list, whatever value it returns will be popped

                // This switch works like if else chains. And is used instead of if else chains. MiniKotlin only has a singular if_ else_ (no else if)
                switch {
                    case(argN lessOrEq number(0)) {
                        return_(miniIntArray(size = number(0)))
                    }
                    case(argN eq number(1)) {
                        return_(miniIntArrayOf(number(0)))
                    }
                }
                // there is also table switch in MiniKotlin.

                val sb = immutableRefTo(StringBuilder()).toVariable("sb")


                // You can assign set operation result to a kotlin variable which will represent mini kotlin variable
                // val array = miniIntArray(size = argN).toVariable("array") <--- this is the same
                // val arrayReusable = miniIntArray(size = argN).toReusable() <--- this is NOT the same.
                //                 It means that upon every usage of array reference its builder will be called
                //                 which mean your code will create the same, but new, array every time you do something with it
                val array = variable<IntArray>("array") setTo miniIntArray(size = argN)
                array[number(0)] = number(0)
                array[number(1)] = number(1)

                // Nt stands for native.
                val counter = variableNt<Int>("counter") setTo number(2)

                // MiniKotlin currently has only while loops (also forEach <- only for arrays tho)
                whileDo(counter lessThan array.size()) {
                    array[counter] = array[counter - number(1)] + array[counter - number(2)]
                    counter setTo counter + number(1)
                }

                // You can call other function by specifying them via Kotlin reflections,
                //  just specify appropriate number of arguments in callN.
                //  Also do not forget to call Nt version if function returns native (not the case here)
                val arrayList = variable<ArrayList<Int>>("arrayList") setTo call1(::ArrayList, array.size())

                // You can specify immutable reusable references to kotlin lambdas and call them from mini kotlin
                // You can also use immutableRefTo() for anything else to capture
                val arrayToListChange = kotlinLambda { ar: IntArray, arList: ArrayList<Int> ->
                    ar.forEach {
                        arList.add(it)
                    }
                }

                arrayToListChange.call(array, arrayList).exec()
                echoLine(str("Result of fib: "), arrayList.toStr())

                // mapA is the same as map, just produces array instead of list
                val s = arrayOf("k", "o", "t", "l", "i", "n").mapA { str(it) }
//            miniObjectArray<String>(*s).exec()

                val someArray = miniObjectArray<String>(*s).toVariable("Different")

                someArray.forEach {
                    echoLine(it)
                }


                val toPrint = buildList {
                    repeat(6) {
                        add(someArray[number(it)])
                    }
                }.toTypedArray()
                echoLine(joinStringsOf(*toPrint))

                return_(array)
            }
        }
        val result = cl.result()
        result.printCode()
        result.saveToTestFolderIfAny()
//        result.printCode()
        val ins = result.initAndGetAsInterface<Some>()
        ins.fib(9)
        ins.other()

        val h = MethodHandles.lookup().findStatic(ins.javaClass, "willErr", MethodType.genericMethodType(0))

//        val thrown = assertThrows<Error> { h.invoke() }
//        assertEquals("Error from bytecode", thrown.message)

    }
}

