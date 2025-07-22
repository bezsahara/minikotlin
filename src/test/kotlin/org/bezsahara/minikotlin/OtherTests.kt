package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.implLambdaMiniKt
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.lib.asJavaObj
import org.bezsahara.minikotlin.lan.lib.bool
import org.bezsahara.minikotlin.lan.lib.echo
import org.bezsahara.minikotlin.lan.lib.echoLine
import org.bezsahara.minikotlin.lan.lib.kinds.getClass
import org.bezsahara.minikotlin.lan.lib.kinds.miniClassNtOf
import org.bezsahara.minikotlin.lan.lib.kinds.miniClassOf
import org.bezsahara.minikotlin.lan.lib.nullValue
import org.bezsahara.minikotlin.lan.lib.number
import org.bezsahara.minikotlin.lan.lib.objectRefOf
import org.bezsahara.minikotlin.lan.lib.return_
import org.bezsahara.minikotlin.lan.lib.str
import org.bezsahara.minikotlin.lan.logic.call
import org.bezsahara.minikotlin.lan.logic.call1
import org.bezsahara.minikotlin.lan.logic.call2
import org.bezsahara.minikotlin.lan.logic.call3
import org.bezsahara.minikotlin.lan.logic.eq
import org.bezsahara.minikotlin.lan.logic.isNotNull
import org.bezsahara.minikotlin.lan.pieces.exec
import org.bezsahara.minikotlin.lan.toVariable
import javax.swing.JFrame
import javax.swing.JLabel
import kotlin.test.Test
import kotlin.test.assertEquals




object OtherTests {
    interface TypeLdc {
        fun test(case: Int): String
    }

    @Test
    fun `ldc handles multiple types and arrays`() {
        val result = makeTestClass<TypeLdc>(func = TypeLdc::test, cl = TypeLdc::class) {
            val c = variableNt<Int>("case")

            switch(c) {
                case(0) {
                    val stringCls = miniClassOf<String>().toVariable("stringCls")
                    return_(call1(Class<String>::getName, stringCls))
                }
                case(1) {
                    val intCls = miniClassNtOf<Int>().toVariable("intCls")
                    return_(call1(Class<Int>::getName, intCls))
                }
                case(2) {
                    val arrayCls = miniClassOf<Array<String>>().toVariable("arrayCls")
                    return_(call1(Class<Array<String>>::getName, arrayCls))
                }
                case(3) {
                    val doubleArrayCls = miniClassOf<DoubleArray>().toVariable("doubleArrCls")
                    return_(call1(Class<DoubleArray>::getName, doubleArrayCls))
                }
                case(4) {
                    val listCls = miniClassOf<List<*>>().toVariable("listCls")
                    return_(call1(Class<List<*>>::getName, listCls))
                }
                default {
                    return_(str("unknown"))
                }
            }
        }

        val ins = result.initAndGetAsInterface<TypeLdc>()

        assertEquals("java.lang.String", ins.test(0))
        assertEquals("int", ins.test(1))
        assertEquals("[Ljava.lang.String;", ins.test(2)) // array of String
        assertEquals("[D", ins.test(3))                  // primitive double[]
        assertEquals("java.util.List", ins.test(4))
        assertEquals("unknown", ins.test(99))
    }




    @Test
    fun `return string`() {
        val printer: (String) -> Unit = implLambdaMiniKt {
            val strArgument = variable<String>("arg0")



            echo(str("printer says: "))
            echoLine(strArgument)

            return_(objectRefOf(Unit))
        }

        println(printer("str is shorter than string"))
    }

    @Test
    fun `awt window creation`() {
        val ins: (String?) -> Unit = implLambdaMiniKt {
            val labelText = variable<String>("arg0") // passed as an argument

            val frame = variable<JFrame>("frame") setTo call1(::JFrame, str("MiniKotlin Window"))
            call3(JFrame::setSize, frame, number(400), number(300)).exec()

            call2(JFrame::setDefaultCloseOperation, frame, number(JFrame.EXIT_ON_CLOSE)).exec()
            if_(labelText.isNotNull()) {
                call2(JFrame::add, frame, call1(::JLabel, labelText)).exec()
            }

            call2(JFrame::setVisible, frame, bool(true)).exec()
            return_(nullValue<Any>())
        }

        ins.invoke("MiniKotlin label")
    }


    object SomeOther {
        fun something() {

        }
    }

    @Test
    fun `object ref works fine`() {
        val ins: (Class<*>) -> String = implLambdaMiniKt {
            val cl = variable<Class<*>>("arg0")
            val k = objectRefOf(SomeOther)

            if_(cl eq k.getClass()) {
                thisFun.return_(str("true"))
            }

            thisFun.return_(str("false"))
        }

        assertEquals("true", ins(SomeOther::class.java))
        assertEquals("false", ins(OtherTests::class.java))
    }
}