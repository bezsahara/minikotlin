package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.StackInfo
import kotlin.reflect.KClass

//fun kByteArray(vararg values: Byte) = object : KValue.ValueBlockAssignable<ByteArray>() {
//    override val objType = TypeInfo.KArray(typeInfo<Byte>())
//    override fun KBMethod.init(variables: Map<String, Int>, variableIdx: Int): Int {
//        ldcOptimized(values.size.toLong())
//        newarray<Byte>()
//        astore(variableIdx)
//        values.forEachIndexed { i, v ->
//            aload(variableIdx)
//            ldcOptimized(i)
//            bipush(v)
//            bastore()
//        }
//        return variableIdx + 1
//    }
//}.let { KRef.Obj(ByteArray::class, it) }
//

fun tyu(l: Array<Any>) {
    l.size
    l[2] = 0
}

class GetArraySize(arr: KRef<*>) : KValue.ValueBlockReturns(arrayOf(arr)) {
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        arraylength()
    }

    override val objType: TypeInfo = TypeInfo.Int
}

class GetArrayItemNt(val arr: KRef<*>, itemIndex: KRef.Native<*>, val actualClass: KClass<*>, val isObj: Boolean = false) : KValue.ValueBlockReturns(arrayOf(arr, itemIndex)) {


    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo
    ) {
        if (isObj) {
            aaload()
            return
        }
        when (arr.kClass) {
            ByteArray::class    -> baload()
            BooleanArray::class -> baload()
            CharArray::class    -> caload()
            ShortArray::class   -> saload()
            IntArray::class     -> iaload()
            LongArray::class    -> laload()
            FloatArray::class   -> faload()
            DoubleArray::class  -> daload()
            else -> error("Unsupported array type: ${arr.kClass}")
        }
    }
    override val objType: TypeInfo = if (isObj) {
        TypeInfo.Java(actualClass.javaObjectType)
    } else {
        TypeInfo.Kt(actualClass)
    }
}

class SetArrayItemNt(
    val arr: KRef<*>,
    val index: KRef<*>,
    val value: KRef<*>,
    val isObj: Boolean = false
) : KValue.ValueBlock(arrayOf(arr, index, value)) {
    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo
    ) {
        if (isObj) {
            aastore()
            return
        }
        when (arr.kClass) {
            ByteArray::class    -> bastore()
            BooleanArray::class -> bastore()
            CharArray::class    -> castore()
            ShortArray::class   -> sastore()
            IntArray::class     -> iastore()
            LongArray::class    -> lastore()
            FloatArray::class   -> fastore()
            DoubleArray::class  -> dastore()
            else -> error("Unsupported array type: ${arr.kClass}")
        }
    }
}

@JvmName("getObj")
inline operator fun <reified T: Any> KRef.Obj<Array<T>>.get(index: KRef.Native<Int>): KRef.Obj<T> {
    return KRef.Obj(T::class, GetArrayItemNt(use(), index, T::class, true))
}

@JvmName("getByte")
operator fun KRef.Obj<ByteArray>.get(index: KRef.Native<Int>): KRef.Native<Byte> {
    return KRef.Native(Byte::class, GetArrayItemNt(use(), index, Byte::class))
}

@JvmName("getShort")
operator fun KRef.Obj<ShortArray>.get(index: KRef.Native<Int>): KRef.Native<Short> {
    return KRef.Native(Short::class, GetArrayItemNt(use(), index, Short::class))
}

@JvmName("getInt")
operator fun KRef.Obj<IntArray>.get(index: KRef.Native<Int>): KRef.Native<Int> {
    return KRef.Native(Int::class, GetArrayItemNt(use(), index, Int::class))
}

@JvmName("getLong")
operator fun KRef.Obj<LongArray>.get(index: KRef.Native<Int>): KRef.Native<Long> {
    return KRef.Native(Long::class, GetArrayItemNt(use(), index, Long::class))
}

@JvmName("getFloat")
operator fun KRef.Obj<FloatArray>.get(index: KRef.Native<Int>): KRef.Native<Float> {
    return KRef.Native(Float::class, GetArrayItemNt(use(), index, Float::class))
}

@JvmName("getDouble")
operator fun KRef.Obj<DoubleArray>.get(index: KRef.Native<Int>): KRef.Native<Double> {
    return KRef.Native(Double::class, GetArrayItemNt(use(), index, Double::class))
}

@JvmName("getChar")
operator fun KRef.Obj<CharArray>.get(index: KRef.Native<Int>): KRef.Native<Char> {
    return KRef.Native(Char::class, GetArrayItemNt(use(), index, Char::class))
}

@JvmName("getBoolean")
operator fun KRef.Obj<BooleanArray>.get(index: KRef.Native<Int>): KRef.Native<Boolean> {
    return KRef.Native(Boolean::class, GetArrayItemNt(use(), index, Boolean::class))
}


@JvmName("setObj")
context(mk: MiniKotlin<*>)
inline operator fun <reified T: Any> KRef.Obj<Array<T>>.set(index: KRef.Native<Int>, value: KRef.Obj<T>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value, true))
}

@JvmName("setByte")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<ByteArray>.set(index: KRef.Native<Int>, value: KRef.Native<Byte>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setShort")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<ShortArray>.set(index: KRef.Native<Int>, value: KRef.Native<Short>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setInt")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<IntArray>.set(index: KRef.Native<Int>, value: KRef.Native<Int>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setLong")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<LongArray>.set(index: KRef.Native<Int>, value: KRef.Native<Long>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setFloat")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<FloatArray>.set(index: KRef.Native<Int>, value: KRef.Native<Float>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setDouble")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<DoubleArray>.set(index: KRef.Native<Int>, value: KRef.Native<Double>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setChar")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<CharArray>.set(index: KRef.Native<Int>, value: KRef.Native<Char>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("setBoolean")
context(mk: MiniKotlin<*>)
operator fun KRef.Obj<BooleanArray>.set(index: KRef.Native<Int>, value: KRef.Native<Boolean>) {
    mk.performAction(SetArrayItemNt(use(), index.use(), value))
}

@JvmName("sizeB")
context(mk: MiniKotlin<*>)
fun KRef.Obj<ByteArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBS")
context(mk: MiniKotlin<*>)
fun KRef.Obj<ShortArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBI")
context(mk: MiniKotlin<*>)
fun KRef.Obj<IntArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBL")
context(mk: MiniKotlin<*>)
fun KRef.Obj<LongArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBF")
context(mk: MiniKotlin<*>)
fun KRef.Obj<FloatArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBD")
context(mk: MiniKotlin<*>)
fun KRef.Obj<DoubleArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBC")
context(mk: MiniKotlin<*>)
fun KRef.Obj<CharArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBB")
context(mk: MiniKotlin<*>)
fun KRef.Obj<BooleanArray>.size() = KRef.Native(Int::class, GetArraySize(use()))

@JvmName("sizeBA")
context(mk: MiniKotlin<*>)
fun <T> KRef.Obj<Array<T>>.size() = KRef.Native(Int::class, GetArraySize(use()))


private inline fun makePrimitiveArray(
    clazz: KClass<*>,
    theArray: KClass<*>,
    values: Array<out KRef<*>>,
    crossinline store: KBMethod.() -> Unit
): KRef.Obj<*> {
    return object : KValue.ValueBlockReturns(values as Array<KRef<*>>) {
        override val autoPush: Boolean = false
        override fun KBMethod.returns(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            ldcOptimized(values.size)
            newarray(clazz)
//            dup()
            repeat(values.size) {
                dup()
                ldcOptimized(it)
                stackInfo.pushArgument(it)
                store()
            }
        }

        override val objType: TypeInfo = TypeInfo.KArray(TypeInfo.Kt(clazz))
    }.let { KRef.Obj(theArray, it) }
}

class NoItemArray(
    val clazz: Class<*>,
    val size: KRef.Native<Int>
) : KValue.ValueBlockReturns(size) {
    override val objType: TypeInfo = TypeInfo.KArray(TypeInfo.Java(clazz))
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
//        ldcOptimized(size)
//        debugStackPrint("A1")
        if (clazz.isPrimitive) {
            newarray(clazz)
        } else {
            anewarray(TypeInfo.Java(clazz))
        }
//        debugStackPrint("A2")
    }
}

fun miniIntArray(size: KRef.Native<Int>) = KRef.Obj(IntArray::class, NoItemArray(Int::class.java, size))
fun miniByteArray(size: KRef.Native<Int>) = KRef.Obj(ByteArray::class, NoItemArray(Byte::class.java, size))
fun miniBooleanArray(size: KRef.Native<Int>) = KRef.Obj(BooleanArray::class, NoItemArray(Boolean::class.java, size))
fun miniShortArray(size: KRef.Native<Int>) = KRef.Obj(ShortArray::class, NoItemArray(Short::class.java, size))
fun miniLongArray(size: KRef.Native<Int>) = KRef.Obj(LongArray::class, NoItemArray(Long::class.java, size))
fun miniFloatArray(size: KRef.Native<Int>) = KRef.Obj(FloatArray::class, NoItemArray(Float::class.java, size))
fun miniDoubleArray(size: KRef.Native<Int>) = KRef.Obj(DoubleArray::class, NoItemArray(Double::class.java, size))
fun miniCharArray(size: KRef.Native<Int>) = KRef.Obj(CharArray::class, NoItemArray(Char::class.java, size))

// Object array (e.g., String[])
inline fun <reified T : Any> miniObjectArray(size: KRef.Native<Int>) =
    KRef.Obj(Array<T>::class, NoItemArray(T::class.javaObjectType, size))

class CreateObjectArray(
    val values: Array<out KRef<*>>,
    val clazz: Class<*>
) : KValue.ValueBlockReturns(values as Array<KRef<*>>) {
    private val typeInfoJava = TypeInfo.Java(clazz)
    override val objType: TypeInfo = TypeInfo.KArray(typeInfoJava)
    override val autoPush: Boolean = false

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        ldcOptimized(values.size)
        anewarray(typeInfoJava)
//        astore(variableIndex)
        repeat(values.size) {
            dup()
//            aload(variableIndex)
            ldcOptimized(it)
            stackInfo.pushArgument(it)
            aastore()
        }
//        if (stackInfo.assignIndex != Int.MIN_VALUE) {
//            astore(stackInfo.assignIndex)
//        }
    }
}

// Jetbrains think that having a ticket for 4 years is okay
// while also acknowledging that it is design flaw themselves.
inline fun <reified T> realArrayClass(): Class<Array<T>> {
//    return Array<T>::class.java cannot do it lol

    return emptyArray<T>().javaClass
}

inline fun <reified T: Any> MiniKotlin<*>.miniObjectArray(vararg values: KRef.Obj<T>): KRef.Obj<Array<T>> {
    return KRef.Obj(realArrayClass<T>(), CreateObjectArray(values, T::class.javaObjectType))
}

fun miniByteArrayOf(vararg values: KRef.Native<Byte>) = makePrimitiveArray(Byte::class, ByteArray::class, values) { bastore() } as KRef.Obj<ByteArray>

fun miniShortArrayOf(vararg values: KRef.Native<Short>) = makePrimitiveArray(Short::class, ShortArray::class, values) { sastore() } as KRef.Obj<ShortArray>

fun miniIntArrayOf(vararg values: KRef.Native<Int>) = makePrimitiveArray(Int::class, IntArray::class, values) { iastore() } as KRef.Obj<IntArray>

fun miniLongArrayOf(vararg values: KRef.Native<Long>) = makePrimitiveArray(Long::class, LongArray::class, values) { lastore() } as KRef.Obj<LongArray>

fun miniFloatArrayOf(vararg values: KRef.Native<Float>) = makePrimitiveArray(Float::class, FloatArray::class, values) { fastore() } as KRef.Obj<FloatArray>

fun miniDoubleArrayOf(vararg values: KRef.Native<Double>) = makePrimitiveArray(Double::class, DoubleArray::class, values) { dastore() } as KRef.Obj<DoubleArray>

fun miniCharArrayOf(vararg values: KRef.Native<Char>) = makePrimitiveArray(Char::class, CharArray::class, values) { castore() } as KRef.Obj<CharArray>

fun miniBooleanArrayOf(vararg values: KRef.Native<Boolean>) = makePrimitiveArray(Boolean::class, BooleanArray::class, values) { bastore() } as KRef.Obj<BooleanArray>
