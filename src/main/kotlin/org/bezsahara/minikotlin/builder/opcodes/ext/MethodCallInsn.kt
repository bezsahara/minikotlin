@file:Suppress("SpellCheckingInspection")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.CAs
import org.bezsahara.minikotlin.builder.opcodes.codes.MethodInsnOp
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.method.KBInvokeDyn
import org.bezsahara.minikotlin.builder.opcodes.method.KBMethodCallOP
import org.bezsahara.minikotlin.lan.compiler.JvmInvoke
import org.bezsahara.minikotlin.lan.compiler.describeKFunction
import org.bezsahara.minikotlin.lan.compiler.getFunctionOwner
import org.bezsahara.minikotlin.lan.compiler.jvmInvoke
import java.lang.invoke.CallSite
import java.lang.invoke.MethodHandles
import java.lang.invoke.MethodType
import java.lang.invoke.StringConcatFactory
import kotlin.Any
import kotlin.reflect.KFunction

// Specify boxable integer as nullable
// Currently works strange
fun <T: Function<*>> KBMethod.invokeFun(f: T) {
    f as KFunction<*>
    val jvmInvoke = f.jvmInvoke()
    val owner = getFunctionOwner(f) ?: error("Could not get function owner")
    val descriptor = describeKFunction(f, jvmInvoke, owner)
    when (jvmInvoke) {
        JvmInvoke.INVOKESTATIC -> invokestatic(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKEINTERFACE -> invokeinterface(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKESPECIAL -> invokespecial(TypeInfo.Java(owner), f.name, descriptor)
        JvmInvoke.INVOKEVIRTUAL -> invokevirtual(TypeInfo.Java(owner), f.name, descriptor)
    }
}

fun KBMethod.invokevirtual(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKEVIRTUAL,
        owner,
        name,
        descriptor,
        isInterface //isInterface
    ))
}

fun KBMethod.invokespecial(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKESPECIAL,
        owner,
        name,
        descriptor,
        isInterface
    ))
}

fun KBMethod.invokestatic(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = false
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKESTATIC,
        owner,
        name,
        descriptor,
        isInterface = isInterface
    ))
}

fun KBMethod.invokeinterface(
    owner: TypeInfo,
    name: String,
    descriptor: MethodDescriptor,
    isInterface: Boolean = true
) {
    addOperation(KBMethodCallOP(
        MethodInsnOp.INVOKEINTERFACE,
        owner,
        name,
        descriptor,
        isInterface = isInterface
    ))
}

fun KBMethod.invokedynamic(
    name: String,
    descriptor: MethodDescriptor,
    bootstrapOwner: TypeInfo,
    bootstrapName: String,
    bootstrapDescriptor: MethodDescriptor,
    isInterface: Boolean,
    bootstrapArgs: Array<Any>
) {
    addOperation(KBInvokeDyn(
        name,
        descriptor,
        bootstrapOwner,
        bootstrapName,
        bootstrapDescriptor,
        isInterface,
        bootstrapArgs
    ))
}


// helper method to generate string concat stuff
// \u0001 <- is a placeholder in recipe for args
fun KBMethod.invokedynamicStringConcat(
    descriptor: MethodDescriptor,
    reciepe: String
) {
    addOperation(KBInvokeDyn(
        "makeConcatWithConstants",
        descriptor,
        TypeInfo.Java(StringConcatFactory::class.java),
        "makeConcatWithConstants",
        strConcatD,
        false,
        arrayOf(reciepe)
    ))
}

class ConcatStringBuilder() {
    private val arrayList = arrayListOf<Any>()
    private var typeSize = 0

    fun arg(typeInfo: TypeInfo) {
        typeSize += 1
        arrayList.add(typeInfo)
    }

    fun str(s: String) {
        arrayList.add(s)
    }

    fun build(): Pair<MethodDescriptor, String> {
        val types = arrayOfNulls<TypeInfo>(typeSize) as Array<TypeInfo>
        var typesIndex = 0
        val s = buildString {
            arrayList.forEach {
                when (it) {
                    is String -> {
                        append(it)
                    }
                    is TypeInfo -> {
                        types[typesIndex] = it
                        typesIndex += 1
                        append("\u0001")
                    }
                    else -> {
                        error("Did not recognise $it as string or type info")
                    }
                }
            }
        }
        return MethodDescriptor(types, TypeInfo.String) to s
    }
}

inline fun KBMethod.invokedynamicStringConcat(
    builder: ConcatStringBuilder.() -> Unit
) {
    val b = ConcatStringBuilder()
    b.builder()
    val built = b.build()
    invokedynamicStringConcat(built.first, built.second)
}

private val strConcatD = MDString(
    $$"(Ljava/lang/invoke/MethodHandles$Lookup;Ljava/lang/String;Ljava/lang/invoke/MethodType;Ljava/lang/String;[Ljava/lang/Object;)Ljava/lang/invoke/CallSite;",
    arrayOf(
        SWord.A(MethodHandles.Lookup::class.java),
        SWord.A(String::class.java), SWord.A(MethodType::class.java),
        SWord.A(String::class.java), CAs.A
    ),
    SWord.A(CallSite::class.java)
)