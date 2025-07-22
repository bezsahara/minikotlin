package org.bezsahara.minikotlin.builder

import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.aload
import org.bezsahara.minikotlin.builder.opcodes.ext.areturn
import org.bezsahara.minikotlin.builder.opcodes.ext.checkcast
import org.bezsahara.minikotlin.builder.opcodes.ext.invokestatic
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.runsMiniKt
import java.lang.reflect.Modifier
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KType
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.typeOf


// For Function implementing lambdas params are named "arg" + number of that argument such as arg0 or arg1 : argN
//inline fun <reified T: Function<*>> implLambda(noinline block: KBMethod.() -> Unit): T {
//    return implLambda(T::class.java, block)
//}

// Does not work as needed
private fun <T: Function<*>> implLambda(functionJava: Class<T>, block: KBMethod.() -> Unit): T {
    val invokeMethod = functionJava.methods.let {
        if (it.size == 1) {
            it[0]
        } else {
            error("")
        }
    }
    require(functionJava.isInterface) { "Lambda type $functionJava is not an interface" }
    if (Modifier.isFinal(invokeMethod.modifiers)) {
        error("Method is final")
    }
    val result = KBClass.Builder("lambda${UUID.randomUUID().toString().replace("-", "")}", ClassProperties.Default).implements(functionJava).body {
        autoInit()
        var pId = -1
        val name1 = invokeMethod.name
        val md = KBMethod.Builder<Any>(
            name1,
            DeclarationProperty(Visibility.Public, isFinal = true, typeInfo = TypeInfo.Java(invokeMethod.returnType)),
            invokeMethod.parameters.map { parameter ->
                println("Param name is ${parameter.name}")
                pId++
                KBMethod.Parameter(parameter.name, TypeInfo.Java(parameter.type), pId, false)//.also { println("S - $it") }
            },
            ClassProperties.Default,
            kbClass=this
        )
        md.runs(block)
        addMethod(name1, md)
    }.result()
    return result.initAndGetAsInterface(functionJava, functionJava.classLoader)
}


inline fun <reified T: Function<*>> implLambdaMiniKt(noinline block: MiniKotlin<T>.() -> Unit): T {
    return implLambdaMiniKt(T::class.java, typeOf<T>(), block)
}

//fun <T: Function<*>> implLambdaMiniKt(functionJava: T, block: MiniKotlin<T>.() -> Unit): T {
//    TODO()
////    return implLambdaMiniKt(functionJava::class.java as Class<T>, block)
//}

@OptIn(ExperimentalReflectionOnLambdas::class)
fun <T: Function<*>> implLambdaMiniKt(functionJava: Class<T>, kType: KType, block: MiniKotlin<T>.() -> Unit): T {
    require(functionJava.isInterface) { "Lambda type $functionJava is not an interface" }
    val kTypeMapList = kType.arguments.map { (it.type?.classifier as? KClass<*>)?.javaObjectType ?: Any::class.java }
    val params = kTypeMapList.dropLast(1)
    val rType = kTypeMapList.last()
    val invokeMethod = functionJava.methods.let {
        if (it.size == 1) {
            it[0]
        } else {
            error("There are more than one method to implement!")
        }
    }
    if (Modifier.isFinal(invokeMethod.modifiers)) {
        error("Method is final")
    }
    val result = KBClass.Builder("lambda${UUID.randomUUID().toString().replace("-", "")}", ClassProperties.Default).implements(functionJava).body {
        autoInit()
        var pId = -1
        val nameOfBridge = invokeMethod.name + "OF"
        val mdBridge = KBMethod.Builder<T>(
            nameOfBridge,
            DeclarationProperty(Visibility.Private, isFinal = true, isStatic = true, typeInfo = TypeInfo.Java(invokeMethod.returnType)),
            params.map { parameterType ->
                pId++
                KBMethod.Parameter("arg$pId", TypeInfo.Java(parameterType), pId, false)
            },
            ClassProperties.Default,
            kbClass=this
        )
        mdBridge.runsMiniKt(block)
        addMethod(nameOfBridge, mdBridge)


        // 0 is this object
        pId = 0
        val name2 = invokeMethod.name
        val md2 = KBMethod.Builder<T>(
            name2,
            DeclarationProperty(Visibility.Public, isFinal = true, typeInfo = TypeInfo.Java(invokeMethod.returnType)),
            params.map { parameterType ->
                pId++
                KBMethod.Parameter("arg$pId", TypeInfo.Object, pId, false)
            },
            ClassProperties.Default,
            kbClass=this
        )
        md2 runs {
            repeat(params.size) {
                aload(it+1, "lArg_${it.plus(1)}")
                checkcast(TypeInfo.Java(params[it]))
            }
            invokestatic(ThisClass, "invokeOF", MDInfo(
                Array(params.size) { TypeInfo.Java(params[it]) },
                TypeInfo.Object
            ))
            areturn()
        }
        addMethod(name2, md2)
    }.result()
    return result.initAndGetAsInterface(functionJava, functionJava.classLoader)
}


fun <T: Function<*>, C: Any> singleFunImplMiniKt(klass: KClass<C>, fn: T, block: MiniKotlin<out T>.() -> Unit): KBClass.Result {
    val clazz = klass.java
    val kFunc = fn as KFunction<*>
    val funcName = kFunc.name
    clazz.methods.firstOrNull { it.name == funcName } ?: error("method ${(fn as KFunction<*>)} does not exist in $klass")
    val invokeMethod = fn.javaMethod!!
    require(clazz.isInterface) { "Lambda type $clazz is not an interface" }
    if (Modifier.isFinal(invokeMethod.modifiers)) {
        error("Method is final")
    }
    val result = KBClass.Builder("lambda${UUID.randomUUID().toString().replace("-", "")}", ClassProperties.Default).implements(clazz).body {
        autoInit()
        implOf(fn).runsMiniKt(block)
    }.result()
    return result
}
