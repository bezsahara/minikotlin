package org.bezsahara.minikotlin.builder.opcodes.codes

import org.bezsahara.minikotlin.builder.declaration.TypeInfo

// IMPORTANT: mini kotlin uses a bit different descriptors. L is for long (not J), A is for object (not L)

/**
 * Models an *abstract* JVM stack word as it is tracked by the bytecode verifier.
 *
 * When an instruction **pushes** a value, the raw [SWord] instance is pushed. By raw, it is meant a known type.
 * On **pop**, however, the verifier interprets the value according
 * to the categories described below:
 *
 * * **Category‑1 / 32‑bit** words implement [W32].
 * * **Category‑2 / 64‑bit** words implement [W64].
 * * [W64Both] represents a value that is known to occupy 64 bits, **but** may
 *   legally be stored either as *one* 64‑bit slot **or** as *two* adjacent
 *   32‑bit slots.
 * * [V] and [V1] are *variables* used while data‑flow information is still
 *   unresolved (e.g. between basic blocks):
 *   * **V** – size unknown (could be one or two slots).
 *   * **V1** – exactly **one** value, but its size (32‑ or 64‑bit) remains
 *     unknown.
 *
 * Concrete constant objects model the JVM’s primitive and reference types:
 *
 * * [I] – integral types (`int`, `byte`, `short`, `char`).
 * * [F] – `float`.
 * * [A] – object or array reference.
 * * [D] – `double` (64‑bit).
 * * [L] – `long`   (64‑bit).
 *
 * @see InsnOp
 */
sealed interface SWord {
    val descriptor: String get() = error("Not implemented")

    val typeInfo: TypeInfo get() = error("Not implemented")

    fun toStringSpecial(): String {
        return toString()
    }

    fun canAccept(other: SWord): Boolean

    fun compareByKind(other: SWord): Boolean

    /** Marker for **category‑1** (32‑bit) stack words. */
    sealed interface W32 : SWord {
        /** Placeholder that never appears in the verifier’s data‑flow graph. */
        companion object : W32 {
            override fun compareByKind(other: SWord): Boolean {
                return other is W32
            }

            override fun canAccept(other: SWord): Boolean {
                return when (other) {
                    is F, is A, is I -> true
                    else -> false
                }
            }

            override fun toString(): String = "Any(A|F|I|S|C|B|Z)"
        }
    }

    /** Marker for **category‑2** (64‑bit) stack words. */
    sealed interface W64 : SWord {
        /** Placeholder that never appears in the verifier’s data‑flow graph. */
        companion object : W64 {

            override fun compareByKind(other: SWord): Boolean {
                return other is W64
            }

            override fun canAccept(other: SWord): Boolean {
                return when (other) {
                    is D, is L -> true
                    else -> false
                }
            }

            override fun toString(): String = "Any(D|L)"
        }
    }

    /**
     * Explicitly denotes a value that occupies **64 bits**, yet may be stored
     * either as a single 64‑bit slot or as two consecutive 32‑bit slots.
     * Never materialises in the final verified stack — it is only a helper for
     * intermediate analysis.
     */
    object W64Both : SWord {
        override fun compareByKind(other: SWord): Boolean {
            TODO("Cannot be implemented: resolved at runtime")
        }

        override fun canAccept(other: SWord): Boolean {
            TODO("Cannot be implemented: resolved at runtime")
        }

        override fun toString(): String {
            return "Any(W64|2xW32)"
        }
    }

    /**
     * *Variable* whose size and category are completely unknown.  Used where a
     * method’s effect on the operand stack cannot be determined a priori (e.g.
     * unresolved call sites).
     */
    object V : SWord {
        override fun compareByKind(other: SWord): Boolean {
            TODO("Cannot be implemented: resolved at runtime")
        }

        override fun canAccept(other: SWord): Boolean {
            throw NotImplementedError("Cannot be implemented: resolved at runtime")
        }
    }

    /**
     * Like [V], but constrained to represent **exactly one** value (either
     * category‑1 or category‑2).
     */
    object V1 : SWord {
        override fun compareByKind(other: SWord): Boolean {
            TODO("Cannot be implemented: resolved at runtime")
        }

        override fun canAccept(other: SWord): Boolean {
            throw NotImplementedError("Cannot be implemented: resolved at runtime")
        }
    }

    /** Integral (category‑1, 32‑bit) types: `int`, `byte`, `short`, `char`. */
    sealed class I(override val typeInfo: TypeInfo) : W32 {
        override val descriptor: String = "I"

        override fun compareByKind(other: SWord): Boolean {
            return other is I
        }

        override fun canAccept(other: SWord): Boolean {
            return other is I
        }

        data object Z : I(TypeInfo.Boolean)

        data object C : I(TypeInfo.Char)

        data object S : I(TypeInfo.Short)

        data object B : I(TypeInfo.Byte)

        companion object : I(TypeInfo.Int) {
            override fun toString(): String {
                return "I"
            }
        }
    }

    /** 32‑bit `float`. */
    data object F : W32 {
        override val descriptor: String = "F"
        override fun compareByKind(other: SWord): Boolean {
            return other === F
        }

        override fun canAccept(other: SWord): Boolean {
            return other === F
        }
    }

    /** Object or array reference (category‑1). */
    open class A(val clazz: Class<*>, val accepts: Any?) : W32 {
        constructor(clazz: Class<*>) : this(clazz, null)

        override fun compareByKind(other: SWord): Boolean {
            return other is A
        }

        val acceptsAll: Boolean get() = accepts === ACCEPTS_ALL
        val acceptsStrict: Boolean get() = accepts === ACCEPTS_STRICT
        fun acceptsAsArray(): Array<Class<*>>? {
            return if (accepts is Array<*>) {
                accepts as Array<Class<*>>
            } else {
                null
            }
        }

        override fun canAccept(other: SWord): Boolean {
            if (other !is A) return false
            return canAccept(other.clazz)
        }

        override fun toStringSpecial(): String {
            return if (clazz === Any::class.java) "A" else clazz.name
        }

        fun canAccept(clazzOther: Class<*>): Boolean {
            if (accepts == null) {
                return clazz.isAssignableFrom(clazzOther)
            }

            if (accepts === ACCEPTS_ALL) {
                return true
            }

            if (accepts === ACCEPTS_STRICT) {
                return clazzOther === clazz
            }

            if (accepts is Array<*>) {
                if ((accepts as Array<Class<*>>).indexOf(clazzOther) > -1) {
                    return true
                }
            }

            return clazz.isAssignableFrom(clazzOther)
        }

        override fun toString(): String {
            return "A"
        }

        companion object : A(Any::class.java, ACCEPTS_ALL) {
            fun createFromTypeInfo(tp: TypeInfo): A {
                return A(tp.recoverJClass())
            }

            fun createFromNullableClass(cl: Class<*>?): A {
                var jClass = cl
                if (jClass == null) {
                    // TODO add a check for if it is allowed
                    jClass = Any::class.java
                }
                return A(jClass)
            }
        }

    }

    /** 64‑bit `double` (category‑2). */
    data object D : W64 {
        override val descriptor: String = "D"
        override fun compareByKind(other: SWord): Boolean {
            return other === D
        }

        override fun canAccept(other: SWord): Boolean {
            return other === D
        }
    }

    /** 64‑bit signed `long` (category‑2). Is actually J in bytecode, I like "L" more tho.*/
    data object L : W64 {
        override val descriptor: String = "L"
        override fun compareByKind(other: SWord): Boolean {
            return other === L
        }

        override fun canAccept(other: SWord): Boolean {
            return other === L
        }
    }
}

@Suppress("ClassName")
object ACCEPTS_STRICT

@Suppress("ClassName")
object ACCEPTS_ALL

object Arr {
    val I = SWord.A(IntArray::class.java)

    val C = SWord.A(CharArray::class.java)
    val B = SWord.A(ByteArray::class.java, arrayOf(BooleanArray::class.java))
    val S = SWord.A(ShortArray::class.java)
    val L = SWord.A(LongArray::class.java)
    val F = SWord.A(FloatArray::class.java)
    val D = SWord.A(DoubleArray::class.java)
    val A = SWord.A(Array<Any>::class.java)

    val All = SWord.A(Array<Any>::class.java, arrayOf(
        IntArray::class.java,
        LongArray::class.java,
        FloatArray::class.java,
        DoubleArray::class.java,
        BooleanArray::class.java,
        ByteArray::class.java,
        ShortArray::class.java,
        CharArray::class.java
    ))
}

object ACommon {
    val TH = SWord.A(Throwable::class.java)

    val Str = SWord.A(String::class.java)
}

fun SWord.toTypeInfo(): TypeInfo? {
    return when (this) {
        is SWord.A -> TypeInfo.Java(this.clazz)
        SWord.F -> TypeInfo.Float
        SWord.I -> TypeInfo.Int
        SWord.D -> TypeInfo.Double
        SWord.L -> TypeInfo.Long
        else -> null
    }
}