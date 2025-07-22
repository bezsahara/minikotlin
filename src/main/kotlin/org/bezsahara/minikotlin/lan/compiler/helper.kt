package org.bezsahara.minikotlin.lan.compiler

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.compiler.asm.mapA
import java.lang.reflect.Modifier
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.KType
import kotlin.reflect.jvm.javaConstructor
import kotlin.reflect.jvm.javaMethod
import kotlin.reflect.jvm.jvmErasure

//// This issue is at https://youtrack.jetbrains.com/issue/KT-42199/KotlinReflectionInternalError-Unknown-origin-of-public-abstract-operator-fun-invoke-on-function-reference-to-FunctionN.invoke
// bigbrains did not fix it for several years
fun functionOwnerIsObject(fn: KFunction<*>): Boolean {
    try {
        val dc = fn.javaMethod?.declaringClass?.kotlin
        return if (dc != null) {
            dc.objectInstance != null
        } else {
            false
        }
    } catch (e: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) {
        return false
    }
}

fun getFunctionOwner(fn: KFunction<*>): Class<*>? {
    return try {
        fn.javaMethod?.declaringClass
            ?: fn.javaConstructor?.declaringClass
    } catch (e: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) {
        try {
            ((fn as kotlin.jvm.internal.CallableReference).owner as ClassBasedDeclarationContainer).jClass
        } catch (_: Throwable) {
            (fn as Function<*>).javaClass.interfaces.firstOrNull { it.name.startsWith("kotlin.jvm.functions.Function") } ?: error("Could not find interface")
        }
    }
}

fun kFunctionIsAccessible(fn: KFunction<*>): Boolean {
    try {
        val javaMethod = fn.javaMethod
        if (javaMethod == null) {
            return true
        }

        val mods = javaMethod.modifiers
        return when {
            Modifier.isPrivate(mods) -> {
                false
            }
            Modifier.isProtected(mods) -> {
                false
            }
            else -> true
        }
    } catch (e: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) {
        return (fn as Function<*>).javaClass.interfaces.any { it.name.startsWith("kotlin.jvm.functions.Function") }
    }
}

fun describeKFunction(fn: KFunction<*>, jvmInvoke: JvmInvoke, jClassOwner: Class<out Any>): MDInfo {
//    val jm = fn.javaMethod!!
    var fnNameTry: String? = null
    var fnSigTry: String? = null
    try {
        fnNameTry = fn.name
        fnSigTry = (fn as kotlin.jvm.internal.CallableReference).signature
        val fnp = if (false) {//jvmInvoke != JvmInvoke.INVOKESTATIC) {
            fn.parameters.let {
                val firstClass = (it.firstOrNull()?.type?.classifier as? KClass<*>)?.java
                if (firstClass == jClassOwner) {
                    it.subList(1, it.size)
                } else it
            }
        } else fn.parameters.filter { it.kind != KParameter.Kind.INSTANCE }
        val params = try {
            fnp.mapA {
                try {
                    TypeInfo.Kt(it.type.classifier as KClass<*>)
                } catch (e: java.lang.ClassCastException) {
                    TypeInfo.Kt((it.type.classifier as KType).jvmErasure)
                }
            }
        } catch (e: Throwable) { // try jvmMethod
            fn.javaMethod!!.parameters.mapA {
                TypeInfo.Java(it.type)
            }
        }

        return MDInfo(
            params,
            TypeInfo.Java(tryGetNormalReturnType(fn))
        )
    } catch (e: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) {
        val j = ((fn as kotlin.jvm.internal.CallableReference).owner as ClassBasedDeclarationContainer).jClass

        if (!j.name.startsWith("kotlin.jvm.functions.Function"))
            error("Due to jetbrains not fixing bugs, this function is not yet supported: $fnNameTry | $fnSigTry. Owner: ${j.name}")

        val m = j.methods.firstOrNull { it.name == "invoke" } ?: error("Unable to find function entry, expected invoke.")
        return MDInfo(
            Array(m.parameters.size) {
                TypeInfo.Object
            },
            TypeInfo.Object
        )
    }
}

fun tryGetNormalReturnType(fn: KFunction<*>): Class<*> {
    fn.javaConstructor?.let {
        return Void.TYPE
    }
    fn.javaMethod?.let {
        return it.returnType
    }
    return (fn.returnType.classifier as? KClass<*>)?.java ?: Any::class.java
}

enum class JvmInvoke { INVOKESTATIC, INVOKEINTERFACE, INVOKESPECIAL, INVOKEVIRTUAL }

/**
 * Returns the JVM invoke opcode that will be used for this KFunction.
 *
 * Top-level / @JvmStatic / extension → INVOKESTATIC
 * Interface method                        → INVOKEINTERFACE
 * Private / constructor / super-call      → INVOKESPECIAL
 * Everything else (normal instance)       → INVOKEVIRTUAL
 */
fun KFunction<*>.jvmInvoke(): JvmInvoke {
    try {// Constructor reference (`::SomeClass`)
        this.javaConstructor?.let { return JvmInvoke.INVOKESPECIAL }

        val m = this.javaMethod ?: return JvmInvoke.INVOKEVIRTUAL  // lambda/unknown → invokevirtual

        val mods = m.modifiers
        return when {
            Modifier.isStatic(mods)               -> JvmInvoke.INVOKESTATIC
            Modifier.isPrivate(mods)              -> JvmInvoke.INVOKESPECIAL
            m.declaringClass.isInterface          -> JvmInvoke.INVOKEINTERFACE
            else                                  -> JvmInvoke.INVOKEVIRTUAL
        }
    } catch (e: kotlin.reflect.jvm.internal.KotlinReflectionInternalError) { // usually just invoke stuff
        return JvmInvoke.INVOKEINTERFACE
    }
}

fun KBMethod.storeAuto(idx: Int, kClass: KClass<*>, name: String?, descriptor: TypeInfo?) {
    when (kClass) {
        Int::class, Boolean::class, Char::class, Short::class, Byte::class -> istore(idx, name, descriptor)
        Long::class -> lstore(idx, name, descriptor)
        Float::class -> fstore(idx, name, descriptor)
        Double::class -> dstore(idx, name, descriptor)
        else -> astore(idx, name, descriptor) // fallback for reference types
    }
}

fun KBMethod.loadAuto(idx: Int, kClass: KClass<*>, name: String? = null) {
    when (kClass) {
        Int::class, Boolean::class, Char::class, Short::class, Byte::class -> iload(idx, name)
        Long::class -> lload(idx, name)
        Float::class -> fload(idx, name)
        Double::class -> dload(idx, name)
        else -> aload(idx, name) // fallback for reference types
    }
}