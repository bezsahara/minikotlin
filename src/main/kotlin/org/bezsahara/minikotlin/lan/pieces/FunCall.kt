package org.bezsahara.minikotlin.lan.pieces

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo
import org.bezsahara.minikotlin.lan.compiler.*
import kotlin.reflect.KFunction

//private var ido = 0

class FunCall(
    val kFunction: KFunction<*>,
    val args: Array<KRef<*>>,
    val asNative: Boolean
) : KValue.ValueBlockReturns(args) {

    override fun toString(): String {
        return "FunCall($fName)"
    }

    private val fName = kFunction.name
    override val autoPush: Boolean = false
    private val owner = TypeInfo.Java(getFunctionOwner(kFunction) ?: error("Unable to get function owner"))
    private val jvmInvoke = kFunction.jvmInvoke()
    private var funDescriptor = describeKFunction(kFunction, jvmInvoke, owner.jClass)

    init {

        val recoverJClass = funDescriptor.returns.recoverJClass()
        if (recoverJClass !== Void.TYPE) {
            if (recoverJClass.isPrimitive) {
                require(asNative) { "Function returns native but is specified to return object. Use callNt version" }
            } else {
                require(!asNative) { "Function returns object but is specified to return native. Use call version (without Nt)" }
            }
        }
    }

    override val objType: TypeInfo = funDescriptor.returns

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val kFunction = kFunction
        if (!kFunctionIsAccessible(kFunction)) {
            val safe = try {
                kFunction.toString()
            } catch (_: Throwable) { null }
            error("Function $safe is not accessible")
        }
        if (fName == "<init>") { // Okay so we are trying to init a constructor, must insert new as well
            new(owner)
            dup()
            funDescriptor = funDescriptor.copy(returns = TypeInfo.Void)
        }
        if (jvmInvoke != JvmInvoke.INVOKESTATIC && functionOwnerIsObject(kFunction)) {
            getstatic(owner, "INSTANCE", owner)
        }
        repeat(args.size) {
            stackInfo.pushArgument(it)
        }
        when (jvmInvoke) {
            JvmInvoke.INVOKESTATIC -> {
                invokestatic(owner, fName, funDescriptor)
            }
            JvmInvoke.INVOKEINTERFACE -> {
                invokeinterface(owner, fName, funDescriptor)
            }
            JvmInvoke.INVOKESPECIAL -> {
                invokespecial(owner, fName, funDescriptor)
            }
            JvmInvoke.INVOKEVIRTUAL -> {
                invokevirtual(owner, fName, funDescriptor)
            }
        }
    }
}