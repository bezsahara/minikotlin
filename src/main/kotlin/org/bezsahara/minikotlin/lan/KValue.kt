package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.logic.NegationIsPossible

inline fun kValueReturns(objTypeInfo: TypeInfo, crossinline returns: KBMethod.() -> Unit): KValue.ValueBlockReturns {
    return object : KValue.ValueBlockReturns() {
        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            returns()
        }

        override val objType: TypeInfo = objTypeInfo
    }
}

inline fun kValueReturns(
    objTypeInfo: TypeInfo,
    args: Array<KRef<*>>,
    crossinline returns: KBMethod.() -> Unit,
): KValue.ValueBlockReturns {
    return object : KValue.ValueBlockReturns(args) {
        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            returns()
        }

        override val objType: TypeInfo = objTypeInfo
    }
}

class PerformAction(private val block: Scope.() -> Unit) : KValue.ValueBlock(null) {
    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        variablesInner = variables
        stackInfoInner = stackInfo
        Scope().block()
    }
    private var variablesInner: Map<String, Int>? = null
    private var stackInfoInner: StackInfo? = null

    inner class Scope {
        val variables: Map<String, Int> get() = variablesInner!!
        val stackInfo: StackInfo get() = stackInfoInner!!
    }
}

sealed interface KValue {

    data class Current<T>(val v: T?) : KValue, NegationIsPossible {
        init {
            require(v == null || v is Number || v is Char || v is Boolean) {
                "Value of current must be either Number, Char, Boolean, or null. But instead it is ${v!!::class}"
            }
        }

        private var notInline = false
        override fun isNotInlinePossible(): Boolean {
            notInline = true
            return v is Boolean
        }

        fun applyToKB(kbMethod: KBMethod) {
            when (v) {
                null -> kbMethod.aconst_null()
                is Number -> kbMethod.ldcOptimized(v)
                is Char -> kbMethod.ldc(v)
                is Boolean -> if (v xor notInline) kbMethod.iconst_1() else kbMethod.iconst_0()
            }
        }
    }

    data object NotPresent : KValue

    sealed class VB : KValue {
        abstract val objType: TypeInfo

        open val autoPush: Boolean = true

        // index 0 is top of stack. last index is bottom of stack
        abstract val stackNeeded: Array<out KRef<*>>?
    }

    sealed class VBReturns : VB()

    // For optimization stuff, this can either be assigned directly to the variable or returned.
    // Currently, does not work.
    abstract class ValueBlockAssignable(
        override val stackNeeded: Array<KRef<*>>?,
    ) : KValue, VBReturns() {
        init {
            error("Does not work")
        }

        constructor() : this(emptyArray())

        abstract fun KBMethod.assignsOrReturns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        )
    }


    // Nothing much. Just executed.
    abstract class ValueBlock(
        override val stackNeeded: Array<KRef<*>>?,
    ) : KValue, VB() {
        final override val objType: TypeInfo = TypeInfo.Void

        abstract fun KBMethod.init(variables: Map<String, Int>, stackInfo: StackInfo)
    }

    // Just push one category in returns on the stack. Do not push anything else
    abstract class ValueBlockReturns(
        override val stackNeeded: Array<KRef<*>>?,
    ) : KValue, VBReturns() {
        constructor(r: KRef<*>?) : this((if (r != null) arrayOf(r) else emptyArray()))
        constructor(pushed3: KRef<*>, pushed2: KRef<*>, pushed1: KRef<*>) : this(
            arrayOf(pushed3, pushed2, pushed1)
        )

        constructor() : this(emptyArray())

        // TODO check if it pushes just one value
        abstract fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo)
    }
}

// TODO check if it works properly
fun KValue.getType(): TypeInfo {
    return when (this) {
        is KValue.Current<*> -> {
            if (v == null) {
                TypeInfo.Object
            } else {
                TypeInfo.Java(v::class.javaPrimitiveType!!)
            }
        }

        is KValue.VB -> objType
        else -> TypeInfo.Object
    }
}