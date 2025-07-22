package org.bezsahara.minikotlin.builder.experiment

import java.lang.invoke.LambdaMetafactory
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import kotlin.jvm.functions.Function2

fun tyu(a: String) {
    val j = a
    val k = 24
    val o = j
    println(o)

}
object LambdaManual {
    @JvmStatic
    fun main(args: Array<String>) {
        val lookup = MethodHandles.lookup()

        // This is the method we'll delegate to
        val implMethod = lookup.findStatic(
            LambdaManual::class.java,
            "myLambdaImpl",
            MethodType.methodType(Unit::class.java, String::class.java, Int::class.javaPrimitiveType)
        )

        // Type of the method we want to implement (Function2.invoke)
        val samMethodType = MethodType.methodType(
            Any::class.java,          // return type
            Any::class.java,
            Any::class.java           // Function2.invoke(Object, Object)
        )

        // Signature of the interface constructor: () -> Function2
        val invokedType = MethodType.methodType(Function2::class.java)

        // Signature of the lambda implementation (bridged type)
        val instantiatedMethodType = MethodType.methodType(
            Unit::class.java,
            String::class.java,
            Int::class.javaObjectType
        )

        val callSite = LambdaMetafactory.metafactory(
            lookup,
            "invoke",
            invokedType,
            samMethodType,
            implMethod,
            instantiatedMethodType
        )

        val factory = callSite.target
        val lambda = factory.invoke() as Function2<String, Int, Unit>

        // Call it
        lambda.invoke("hello", 42)
    }

    @JvmStatic
    fun myLambdaImpl(s: String, i: Int): Unit? {
        println("Lambda called with: $s and $i")
        return Unit
    }
}
