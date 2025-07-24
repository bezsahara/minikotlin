package org.bezsahara.minikotlin.lan

import kotlin.reflect.KClass

fun KClass<out Any>?.isConsideredAsNative(): Boolean {
    return when (this) {
        Int::class, Float::class, Long::class, Double::class, Char::class, Byte::class, Boolean::class, Short::class -> true
        else -> false
    }
}

context(mk: MiniKotlinAny)
fun <T : Any> KRef.Obj<T>.toVariable(name: String? = null): KVar.Obj<T> {
    return mk.run {
        variable<T>(name ?: mk.createVariableName(), this@toVariable.kClass as KClass<T>) setTo this@toVariable
    }
}

context(mk: MiniKotlinAny)
fun <T : Any> KRef.Native<T>.toVariable(name: String? = null): KVar.Native<T> {
    return mk.run {
        variableNt<T>(name ?: mk.createVariableName(), this@toVariable.kClass as KClass<T>) setTo this@toVariable
    }
}

fun <T : Any> KRef.Obj<T>.toReusable(): ReusableRefObj<T> {
    use()
    return ReusableRefObj(jClass, value)
}

fun <T : Any> KRef.Native<T>.toReusable(): ReusableRefNative<T> {
    use()
    return ReusableRefNative(jClass, value)
}

fun <T : Any> reusableRef(ref: KRef.Obj<T>): ReusableRefObj<T> {
    ref.use()
    return ReusableRefObj(ref.jClass, ref.value)
}

fun <T : Any> reusableRefNt(ref: KRef.Native<T>): ReusableRefNative<T> {
    ref.use()
    return ReusableRefNative(ref.jClass, ref.value)
}


// TODO fix constructors
sealed interface KRef<T> {
    var used: Boolean
    val value: KValue

    @Deprecated("Use jClass instead", replaceWith = ReplaceWith("jClass"))
    val kClass: KClass<*>
    val jClass: Class<*>

    fun use(): KRef<T> {
        if (used) {
            throw IllegalStateException("KRef was already used: $this")
        }
        used = true
        return this
    }

    open class Obj<T : Any>(
        override val jClass: Class<T>,
        override val value: KValue,
    ) : KRef<T> {
        @Deprecated("Use jClass instead", replaceWith = ReplaceWith("jClass"))
        override val kClass = jClass.kotlin

        constructor(kClass: KClass<out T>, value: KValue) : this(
            kClass.javaObjectType as Class<T>, value
        )

        override var used: Boolean = false

        override fun toString(): String {
            return "KRef.Obj(jClass=$jClass, value=$value)"
        }
    }

    open class Native<T : Any>(
        override val jClass: Class<out T>,
        override val value: KValue,
    ) : KRef<T> {
        private var markedKClass: KClass<out T>? = null

        constructor(kClass: KClass<out T>, value: KValue) : this(
            kClass.javaPrimitiveType ?: if (kClass == Unit::class) {
                Void.TYPE!! // <-- Kotlin nullability is this dumb (when using java)
            } else {
                error("$kClass is not a native class!")
            } as Class<out T>,
            value
        ) {
            markedKClass = kClass
        }

        @Suppress("OVERRIDE_DEPRECATION")
        override val kClass: KClass<out T> = markedKClass ?: (jClass.kotlin as KClass<T>)

        override var used: Boolean = false

        // TODO figure out what to do with Unit

        override fun toString(): String {
            return "KRef.Native(kClass=$kClass, value=$value)"
        }
    }

    class Null<T : Any>(original: KClass<T>) : Obj<T>(original, KValue.Current(null))

    // placeholder - not to introduce nullability
    data object Nothing : KRef<Nothing> {
        override var used: Boolean = false
        override val jClass: Class<*>
            get() = TODO("Not yet implemented")
        override val value: KValue
            get() = TODO("Not yet implemented")
        override val kClass: KClass<out Nothing>
            get() = TODO("Not yet implemented")
    }
}

// just sets used to false always. Basically allows to be reused.
class ReusableRefObj<T : Any>(jClass: Class<T>, value: KValue) : KRef.Obj<T>(jClass, value) {
    override var used: Boolean
        get() = false
        set(value) {}
}

// Same but native. Can be used for propertyGet so not to rewrite it always
class ReusableRefNative<T : Any>(jClass: Class<out T>, value: KValue) : KRef.Native<T>(jClass, value) {
    override var used: Boolean
        get() = false
        set(value) {}
}

// Place holder
fun <T> kRefNothing(): KRef<T> {
    return KRef.Nothing as KRef<T>
}

// TODO I really need to fix constructors
sealed interface KVar<T : Any> : KRef<T> {
    var initialized: Boolean
    val name: String
    val forcedIndex: Int


    // forces index is an index that was "forced" on the variable even before indexes are resolved. Parameters need it
    class Obj<T : Any>(override val name: String, type: KClass<out T>, override val forcedIndex: Int) :
        KVar<T>, KRef.Obj<T>(type, KValue.NotPresent) {
        @Deprecated("Do not use value.", ReplaceWith("Obj(name, type)"))
        constructor(name: String, type: KClass<out T>, value: KValue) : this(name, type, -1)

        constructor(name: String, type: KClass<out T>) : this(name, type, -1)

        @Deprecated("Do not use value.", ReplaceWith("(name, type, forcedIndex)"))
        constructor(name: String, type: KClass<out T>, value: KValue, forcedIndex: Int) : this(
            name, type, forcedIndex
        )

        override var initialized: Boolean = false
        override var used: Boolean
            get() = false
            set(value) {}

        override fun toString(): String {
            return "KVar.Obj(name=$name, value=$value, type=$kClass)"
        }
    }

    class Native<T : Any>(
        override val name: String,
        type: KClass<out T>,
        value: KValue,
        override val forcedIndex: Int,
    ) : KVar<T>, KRef.Native<T>(type, value) {
        constructor(name: String, type: KClass<out T>, value: KValue) : this(name, type, value, -1)

        override var initialized: Boolean = false
        override var used: Boolean
            get() = false
            set(value) {}

        override fun toString(): String {
            return "KVar.Native(name=$name, value=$value, type=$kClass)"
        }
    }
}
