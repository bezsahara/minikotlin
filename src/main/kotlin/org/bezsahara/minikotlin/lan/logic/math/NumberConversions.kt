package org.bezsahara.minikotlin.lan.logic.math

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

@JvmField val allSets123 = setOf(
    Byte::class.javaPrimitiveType,
    Short::class.javaPrimitiveType,
    Char::class.javaPrimitiveType,
    Boolean::class.javaPrimitiveType
)

class NumberConversions(
    val ref: KRef<*>,
    val to: Class<*>
) : KValue.ValueBlockReturns(arrayOf(ref)) {

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        val from = ref.jClass
        val target = to

        // Optimization: avoid redundant conversion if value is an immediate constant that fits
        val cV = ref.value
        if (cV is KValue.Current<*>) {
            val constant = cV.v
            if (constant is Int && target in allSets123) {
                val fits = when (target) {
                    Byte::class.javaPrimitiveType -> constant in Byte.MIN_VALUE..Byte.MAX_VALUE
                    Short::class.javaPrimitiveType -> constant in Short.MIN_VALUE..Short.MAX_VALUE
                    Char::class.javaPrimitiveType -> constant in Char.MIN_VALUE.code..Char.MAX_VALUE.code
                    Boolean::class.javaPrimitiveType -> constant == 0 || constant == 1
                    else -> false
                }
                if (fits) return // skip conversion
            }
        }

        when (from) {
            Int::class.javaPrimitiveType -> when (target) {
                Byte::class.javaPrimitiveType -> i2b()
                Char::class.javaPrimitiveType -> i2c()
                Short::class.javaPrimitiveType -> i2s()
                Long::class.javaPrimitiveType -> i2l()
                Float::class.javaPrimitiveType -> i2f()
                Double::class.javaPrimitiveType -> i2d()
                Int::class.javaPrimitiveType -> {} // no-op
                else -> error("Unsupported conversion from int to $target")
            }

            Long::class.javaPrimitiveType -> when (target) {
                Int::class.javaPrimitiveType -> l2i()
                Float::class.javaPrimitiveType -> l2f()
                Double::class.javaPrimitiveType -> l2d()
                Byte::class.javaPrimitiveType -> {l2i(); i2b()}
                Char::class.javaPrimitiveType -> {l2i(); i2c()}
                Short::class.javaPrimitiveType -> {l2i(); i2s()}
                else -> error("Unsupported conversion from long to $target")
            }

            Float::class.javaPrimitiveType -> when (target) {
                Int::class.javaPrimitiveType -> f2i()
                Long::class.javaPrimitiveType -> f2l()
                Double::class.javaPrimitiveType -> f2d()
                Byte::class.javaPrimitiveType -> {f2i(); i2b()}
                Char::class.javaPrimitiveType -> {f2i(); i2c()}
                Short::class.javaPrimitiveType -> {f2i(); i2s()}
                else -> error("Unsupported conversion from float to $target")
            }

            Double::class.javaPrimitiveType -> when (target) {
                Int::class.javaPrimitiveType -> d2i()
                Long::class.javaPrimitiveType -> d2l()
                Float::class.javaPrimitiveType -> d2f()
                Byte::class.javaPrimitiveType -> {d2i(); i2b()}
                Char::class.javaPrimitiveType -> {d2i(); i2c()}
                Short::class.javaPrimitiveType -> {d2i(); i2s()}
                else -> error("Unsupported conversion from double to $target")
            }

            else -> error("Unsupported source type: $from")
        }
    }

    override val objType: TypeInfo = TypeInfo.Java(to)
}
@JvmName("asInt1")
fun KRef.Native<Boolean>.asInt(): KRef.Native<Int> {
    @Suppress("UNCHECKED_CAST")
//    return this as KRef.Native<Int>
    return KRef.Native(Int::class, value)
}

@JvmName("asInt12")
fun KRef.Native<Byte>.asInt(): KRef.Native<Int> {
    @Suppress("UNCHECKED_CAST")
//    return this as KRef.Native<Int>
    return KRef.Native(Int::class, value)
}

@JvmName("asInt13")
fun KRef.Native<Short>.asInt(): KRef.Native<Int> {
    @Suppress("UNCHECKED_CAST")
//    return this as KRef.Native<Int>
    return KRef.Native(Int::class, value)
}

@JvmName("asInt14")
fun KRef.Native<Char>.asInt(): KRef.Native<Int> {
    @Suppress("UNCHECKED_CAST")
//    return this as KRef.Native<Int>
    return KRef.Native(Int::class, value)
}

// --- From Byte ---
@JvmName("ByteToShort")
fun KRef.Native<Byte>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("ByteToChar")
fun KRef.Native<Byte>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("ByteToInt")
fun KRef.Native<Byte>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("ByteToLong")
fun KRef.Native<Byte>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("ByteToFloat")
fun KRef.Native<Byte>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))
@JvmName("ByteToDouble")
fun KRef.Native<Byte>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Short ---
@JvmName("ShortToByte")
fun KRef.Native<Short>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("ShortToChar")
fun KRef.Native<Short>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("ShortToInt")
fun KRef.Native<Short>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("ShortToLong")
fun KRef.Native<Short>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("ShortToFloat")
fun KRef.Native<Short>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))
@JvmName("ShortToDouble")
fun KRef.Native<Short>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Char ---
@JvmName("CharToByte")
fun KRef.Native<Char>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("CharToShort")
fun KRef.Native<Char>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("CharToInt")
fun KRef.Native<Char>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("CharToLong")
fun KRef.Native<Char>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("CharToFloat")
fun KRef.Native<Char>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))
@JvmName("CharToDouble")
fun KRef.Native<Char>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Int ---
@JvmName("IntToByte")
fun KRef.Native<Int>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("IntToShort")
fun KRef.Native<Int>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("IntToChar")
fun KRef.Native<Int>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("IntToLong")
fun KRef.Native<Int>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("IntToFloat")
fun KRef.Native<Int>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))
@JvmName("IntToDouble")
fun KRef.Native<Int>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Long ---
@JvmName("LongToByte")
fun KRef.Native<Long>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("LongToShort")
fun KRef.Native<Long>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("LongToChar")
fun KRef.Native<Long>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("LongToInt")
fun KRef.Native<Long>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("LongToFloat")
fun KRef.Native<Long>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))
@JvmName("LongToDouble")
fun KRef.Native<Long>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Float ---
@JvmName("FloatToByte")
fun KRef.Native<Float>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("FloatToShort")
fun KRef.Native<Float>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("FloatToChar")
fun KRef.Native<Float>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("FloatToInt")
fun KRef.Native<Float>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("FloatToLong")
fun KRef.Native<Float>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("FloatToDouble")
fun KRef.Native<Float>.toDouble(): KRef.Native<Double> =
    KRef.Native(Double::class, NumberConversions(this, Double::class.java))

// --- From Double ---
@JvmName("DoubleToByte")
fun KRef.Native<Double>.toByte(): KRef.Native<Byte> =
    KRef.Native(Byte::class, NumberConversions(this, Byte::class.java))
@JvmName("DoubleToShort")
fun KRef.Native<Double>.toShort(): KRef.Native<Short> =
    KRef.Native(Short::class, NumberConversions(this, Short::class.java))
@JvmName("DoubleToChar")
fun KRef.Native<Double>.toChar(): KRef.Native<Char> =
    KRef.Native(Char::class, NumberConversions(this, Char::class.java))
@JvmName("DoubleToInt")
fun KRef.Native<Double>.toInt(): KRef.Native<Int> =
    KRef.Native(Int::class, NumberConversions(this, Int::class.java))
@JvmName("DoubleToLong")
fun KRef.Native<Double>.toLong(): KRef.Native<Long> =
    KRef.Native(Long::class, NumberConversions(this, Long::class.java))
@JvmName("DoubleToFloat")
fun KRef.Native<Double>.toFloat(): KRef.Native<Float> =
    KRef.Native(Float::class, NumberConversions(this, Float::class.java))



fun main() {
    val types = listOf(
        "Byte",
        "Short",
        "Char",
        "Int",
        "Long",
        "Float",
        "Double"
    )

    val nativeClass = "KRef.Native"

    for (from in types) {
        println("// --- From $from ---")
        for (to in types) {
            if (from == to) continue
            println("""
@JvmName("${from}To$to")                
fun $nativeClass<$from>.to$to(): $nativeClass<$to> =
    $nativeClass($to::class, NumberConversions(this, $to::class.java))
            """.trimIndent())
        }
        println()
    }
}
