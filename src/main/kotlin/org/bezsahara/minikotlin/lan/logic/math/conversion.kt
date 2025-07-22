package org.bezsahara.minikotlin.lan.logic.math

import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.pieces.NumberConversion

/* ---------------------------------------------------------------------------
 *  UNIVERSAL NATIVE-TYPE CONVERSIONS
 *  Covers EVERY pair drawn from:
 *    Int, Long, Short, Byte, Float, Double, Char, Boolean
 *  All functions follow the same pattern:
 *      1) grab a fresh op-index from mk
 *      2) emit NumberConversion(source, Target::class, index)
 *      3) return a fresh KRef.Native<Target>
 * ------------------------------------------------------------------------- */

/* ---------- numeric → numeric / char / boolean ---------- */

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toIntNt(): KRef.Native<Int> {
    return KRef.Native(Int::class, NumberConversion(this, Int::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toLongNt(): KRef.Native<Long> {
    return KRef.Native(Long::class, NumberConversion(this, Long::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toShortNt(): KRef.Native<Short> {
    return KRef.Native(Short::class, NumberConversion(this, Short::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toByteNt(): KRef.Native<Byte> {
    return KRef.Native(Byte::class, NumberConversion(this, Byte::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toFloatNt(): KRef.Native<Float> {
    return KRef.Native(Float::class, NumberConversion(this, Float::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toDoubleNt(): KRef.Native<Double> {
    return KRef.Native(Double::class, NumberConversion(this, Double::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toCharNt(): KRef.Native<Char> {
    return KRef.Native(Char::class, NumberConversion(this, Char::class))
}

context(mk: MiniKotlin<*>)
fun <T : Number> KRef.Native<T>.toBooleanNt(): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, NumberConversion(this, Boolean::class))
}

/* ---------- Char → everything ---------- */

context(mk: MiniKotlin<*>) @JvmName("toIntNtChar") fun KRef.Native<Char>.toIntNt(): KRef.Native<Int> {
   return KRef.Native(
        Int::class,
        NumberConversion(this, Int::class)
    )
}

context(mk: MiniKotlin<*>)
@JvmName("LongToNtChar")
fun KRef.Native<Char>.toLongNt(): KRef.Native<Long> {
   return KRef.Native(
        Long::class,
        NumberConversion(this, Long::class)
    )
}

context(mk: MiniKotlin<*>)
@JvmName("CharToShortNt")
fun KRef.Native<Char>.toShortNt(): KRef.Native<Short> {
   return KRef.Native(
        Short::class,
        NumberConversion(this, Short::class)
    )
}

context(mk: MiniKotlin<*>)
@JvmName("CharToByteNt")
fun KRef.Native<Char>.toByteNt(): KRef.Native<Byte> {
    return KRef.Native(Byte::class, NumberConversion(this, Byte::class))
}

context(mk: MiniKotlin<*>)
@JvmName("CharToFloatNt")
fun KRef.Native<Char>.toFloatNt(): KRef.Native<Float> {
    return KRef.Native(Float::class, NumberConversion(this, Float::class))
}

context(mk: MiniKotlin<*>)
@JvmName("CharToDoubleNt")
fun KRef.Native<Char>.toDoubleNt(): KRef.Native<Double> {
    return KRef.Native(Double::class, NumberConversion(this, Double::class))
}

context(mk: MiniKotlin<*>)
@JvmName("CharToBooleanNt")
fun KRef.Native<Char>.toBooleanNt(): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, NumberConversion(this, Boolean::class))
}

/* ---------- Boolean → everything ---------- */

context(mk: MiniKotlin<*>)
@JvmName("BooleanToIntNt")
fun KRef.Native<Boolean>.toIntNt(): KRef.Native<Int> {
   return KRef.Native(
        Int::class,
        NumberConversion(this, Int::class)
    )
}


context(mk: MiniKotlin<*>)
@JvmName("BooleanToLongNt")
fun KRef.Native<Boolean>.toLongNt(): KRef.Native<Long> {
    return KRef.Native(Long::class, NumberConversion(this, Long::class))
}

context(mk: MiniKotlin<*>)
@JvmName("BooleanToShortNt")
fun KRef.Native<Boolean>.toShortNt(): KRef.Native<Short> {
    return KRef.Native(Short::class, NumberConversion(this, Short::class))
}

context(mk: MiniKotlin<*>)
@JvmName("BooleanToByteNt")
fun KRef.Native<Boolean>.toByteNt(): KRef.Native<Byte> {
    return KRef.Native(Byte::class, NumberConversion(this, Byte::class))
}

context(mk: MiniKotlin<*>)
@JvmName("BooleanToFloatNt")
fun KRef.Native<Boolean>.toFloatNt(): KRef.Native<Float> {
    return KRef.Native(Float::class, NumberConversion(this, Float::class))
}

context(mk: MiniKotlin<*>)
@JvmName("BooleanToDoubleNt")
fun KRef.Native<Boolean>.toDoubleNt(): KRef.Native<Double> {
    return KRef.Native(Double::class, NumberConversion(this, Double::class))
}

context(mk: MiniKotlin<*>)
@JvmName("BooleanToCharNt")
fun KRef.Native<Boolean>.toCharNt(): KRef.Native<Char> {
    return KRef.Native(Char::class, NumberConversion(this, Char::class))
}

/* ---------------------------------------------------------------------------
 *  Add more domain-specific conversions here if your runtime supports them
 *  (e.g. unsigned types).  The pattern never changes.
 * ------------------------------------------------------------------------- */