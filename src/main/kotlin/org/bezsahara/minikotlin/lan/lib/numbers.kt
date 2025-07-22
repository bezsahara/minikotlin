package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.declaration.typeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.invokestatic
import org.bezsahara.minikotlin.builder.opcodes.ext.invokevirtual
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

// TODO make sure variable index is okay
class BoxingOfNBC(val n: KRef<*>) : KValue.ValueBlockReturns(n) {
    override val objType: TypeInfo = TypeInfo.Java(n.jClass)
//    override val assignCat2: Boolean = false // we only allocate object ref

    override fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo) {
        when (n.jClass) {
            Long::class.java -> {
//                ldcOptimized(n)
                invokestatic(typeInfo<Long>(), "valueOf", args(Long::class) returns typeInfo<Long>())
            }
            Int::class.java -> {
//                ldcOptimized(n)
                invokestatic(typeInfo<Int>(), "valueOf", args(Int::class) returns typeInfo<Int>())
            }
            Double::class.java -> {
//                ldcOptimized(n)
                invokestatic(typeInfo<Double>(), "valueOf", args(Double::class) returns typeInfo<Double>())
            }
            Float::class.java -> {
//                ldcOptimized(n)
                invokestatic(typeInfo<Float>(), "valueOf", args(Float::class) returns typeInfo<Float>())
            }
            Boolean::class.java -> {
//                ldcOptimized(n)
                invokestatic(typeInfo<Boolean>(), "valueOf", args(Boolean::class) returns typeInfo<Boolean>())
            }
            Char::class.java -> {
//                ldcOptimized(n.code)
                invokestatic(typeInfo<Char>(), "valueOf", args(Char::class) returns typeInfo<Char>())
            }
            else -> error("Unsupported constant type for boxing: ${n::class}")
        }

    }
}

inline fun <reified T: Number> boxNumber(n: T): KRef.Obj<T>  = boxNumber(number(n))

inline fun <reified T: Number> boxNumber(n: KRef.Native<T>): KRef.Obj<T> {
    return KRef.Obj(T::class, BoxingOfNBC(n))
}

fun boxBool(n: Boolean): KRef.Obj<Boolean> = boxBool(bool(n))

fun boxBool(n: KRef.Native<Boolean>): KRef.Obj<Boolean> {
    return KRef.Obj(Boolean::class, BoxingOfNBC(n))
}

fun boxChar(n: Char): KRef.Obj<Char> = boxChar(char(n))

fun boxChar(n: KRef.Native<Char>): KRef.Obj<Char> {
    return KRef.Obj(Char::class, BoxingOfNBC(n))
}
// TODO make sure variable index is okay
class UnboxingOfNBC(val o: KRef.Obj<out Any>) : KValue.ValueBlockReturns(arrayOf(o)) {
    override val objType: TypeInfo = TypeInfo.Java(o.kClass.javaPrimitiveType!!)

    override fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo) {
        // 'o' is expected to have already pushed its reference onto the stack
        when (o.kClass) {
            Long::class -> {
                invokevirtual(typeInfo<Long>(), "longValue", args() returns Long::class)
            }
            Int::class -> {
                invokevirtual(typeInfo<Int>(), "intValue", args() returns Int::class)
            }
            Double::class -> {
                invokevirtual(typeInfo<Double>(), "doubleValue", args() returns Double::class)
            }
            Float::class -> {
                invokevirtual(typeInfo<Float>(), "floatValue", args() returns Float::class)
            }
            Boolean::class -> {
                invokevirtual(typeInfo<Boolean>(), "booleanValue", args() returns Boolean::class)
            }
            Char::class -> {
                invokevirtual(typeInfo<Char>(), "charValue", args() returns Char::class)
            }
            else -> error("Unsupported unboxing type: ${o.kClass}")
        }
    }
}

fun <T: Number> KRef.Obj<T>.unbox(): KRef.Native<T> {
    return unboxNumber(this)
}

fun <T: Number> unboxNumber(n: KRef.Obj<T>): KRef.Native<T> {
    return KRef.Native(n.kClass, UnboxingOfNBC(n))
}
fun unboxChar(n: KRef.Obj<Char>): KRef.Native<Char> {
    return KRef.Native(n.kClass, UnboxingOfNBC(n))
}
fun unboxBool(n: KRef.Obj<Boolean>): KRef.Native<Boolean> {
    return KRef.Native(n.kClass, UnboxingOfNBC(n))
}


fun <T: Number> number(n: T): KRef.Native<T> {
    return KRef.Native(n::class, KValue.Current(n))
}

fun bool(b: Boolean): KRef.Native<Boolean> {
//    println("B: $b")
    return KRef.Native(Boolean::class, KValue.Current(b))
}

fun char(c: Char): KRef.Native<Char> {
    return KRef.Native(Char::class, KValue.Current(c))
}