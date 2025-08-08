package org.bezsahara.minikotlin.builder.declaration

//import org.bezsahara.kbytes.lan.ty
import org.bezsahara.minikotlin.builder.declaration.TypeInfo.JClassAvailable
import org.bezsahara.minikotlin.builder.declaration.TypeInfo.Java
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord.*
import org.bezsahara.minikotlin.compiler.asm.mapA
import org.objectweb.asm.Type
import java.lang.management.ManagementFactory
import kotlin.reflect.KClass

val KClass<out Any>.TI get() = TypeInfo.Kt(this)

fun kArrayOf(typeInfo: TypeInfo) = TypeInfo.KArray(typeInfo)

inline fun <reified T: Any> kArrayOf() = TypeInfo.KArray(TypeInfo.Kt(T::class))

sealed interface TypeHold {
    interface None : TypeHold
}
fun main() {
    val gcBeans = ManagementFactory.getGarbageCollectorMXBeans()
    for (gc in gcBeans) {
        println("GC Name: ${gc.name}")
    }
}

fun TypeInfo.occupiesTwo(): Boolean {
    return when (recoverJClass()) {
        Double::class.java, Long::class.java -> true
        else -> false
    }
}


sealed class TypeInfo : TypeHold {
    abstract fun getStringRep(): String

    abstract fun getReturnStringRep(): String

    // Return either class
    abstract fun recoverJClass(): Class<*>

    // Put fully byte code okay type here
    // path/to/Class
    // Also it is very much required that you enter "recoveredJClass" as at least closest known super type.
    // Verifier will not work properly if this is omitted, and basically half of the library together with it
    data class CustomType(
        val type: String,
        val recoveredJClass: Class<*> = Any::class.java
    ) : TypeInfo() {
        init {
            require(type.isNotEmpty()) {
                "Class name should not be empty!"
            }
        }

        override fun equals(other: Any?): Boolean {
            return if (other is TypeInfo) {
                other.getReturnStringRep().equals(getReturnStringRep())
            } else {
                false
            }
        }

        override fun recoverJClass(): Class<*> {
            return recoveredJClass
        }

        private val anArray = type.first() == '['

        override fun getReturnStringRep(): String {
            if (anArray)
                return type
            return "L$type;"
        }

        override fun getStringRep(): String {
            return type
        }

        override fun hashCode(): Int {
            return getReturnStringRep().hashCode()
        }
    }

    sealed class JClassAvailable : TypeInfo() {
        abstract val jClass: Class<out Any>

        override fun recoverJClass(): Class<*> = jClass

        private inner class Init(private val modeSR: Boolean): () -> String {
            override fun invoke(): String {
                if (modeSR) {
                    return (jClass.name as java.lang.String).replace('.', '/')
                } else {
                    val jClass1 = jClass
                    if (jClass1.isArray) {
                        return jClass1.name.replace('.', '/', false)
                    }

                    if (jClass1.isPrimitive) {
                        return primitiveTypeDescriptors[jClass1] ?: error("$jClass1 is primitive but wasn't in the map")
                    }
//                    kotlin.jvm.internal.TypeIntrinsics.beforeCheckcastToFunctionOfArity
                    return "L${(jClass1.name as java.lang.String).replace('.', '/')};"
                }
            }
        }

        private val sr: String by lazy(Init(true))

        private val rsr by lazy(Init(false))

        override fun equals(other: Any?): Boolean {
            return when (other) {
                is JClassAvailable -> jClass === other.jClass
                is TypeInfo -> getReturnStringRep().equals(other.getReturnStringRep())
                else -> false
            }
        }

        override fun getStringRep(): String {
            return sr
        }

        override fun getReturnStringRep(): String {
            return rsr
        }

        override fun hashCode(): Int {
            return jClass.hashCode() * 31
        }
    }

    data class KArray(val el: TypeInfo) : JClassAvailable() {
        override fun getStringRep(): String {
            return getReturnStringRep()
        }

        override fun getReturnStringRep(): String {
            return "[${el.getReturnStringRep()}"
        }

        override val jClass: Class<out Any> = java.lang.reflect.Array.newInstance(el.recoverJClass(), 0).javaClass

        override fun recoverJClass(): Class<*> {
            return jClass
        }
    }

    sealed interface FieldOkay : TypeHold

//    sealed interface ParamType : TypeHold

    // NOTE: Use Java better. Any Number or bool or char you pass here will be automatically a primitive type.
    //                               And even so it is not guaranteed. KClass behaviour is very annoying.
    data class Kt(val t: KClass<out Any>) : FieldOkay, JClassAvailable() {
        override val jClass: Class<out Any> = t.java
    }

    data class Java(val t: Class<out Any?>) : FieldOkay, JClassAvailable() {
        override val jClass: Class<out Any> = t
    }

    data object Void : JClassAvailable() {
        override val jClass: Class<out Any> = java.lang.Void.TYPE
    }
//    data object None : TypeInfo // Treated as Void

    data object Object : JClassAvailable() {
        override val jClass: Class<out Any> = java.lang.Object::class.java
        override fun getStringRep(): String {
            return "java/lang/Object"
        }

        override fun getReturnStringRep(): String {
            return "Ljava/lang/Object;"
        }
    }
    data object KtAny : JClassAvailable() {
        override val jClass: Class<out Any> = Any::class.java
        override fun getStringRep(): String {
            return "java/lang/Object"
        }

        override fun getReturnStringRep(): String {
            return "Ljava/lang/Object;"
        }
    }

    companion object {

        @JvmField val Int = Kt(kotlin.Int::class)
        @JvmField val Byte = Kt(kotlin.Byte::class)
        @JvmField val Long = Kt(kotlin.Long::class)
        @JvmField val Char = Kt(kotlin.Char::class)
        @JvmField val Short = Kt(kotlin.Short::class)
        @JvmField val Float = Kt(kotlin.Float::class)
        @JvmField val Boolean = Kt(kotlin.Boolean::class)
        @JvmField val Double = Kt(kotlin.Double::class)

        @JvmField val String = Kt(kotlin.String::class)
        @JvmField val Throwable = Java(java.lang.Throwable::class.java)
        @JvmField val Class = Java(java.lang.Class::class.java)

        @JvmField val IntObj = Java(java.lang.Integer::class.java)
        @JvmField val ByteObj = Java(java.lang.Byte::class.java)
        @JvmField val LongObj = Java(java.lang.Long::class.java)
        @JvmField val CharObj = Java(java.lang.Character::class.java)
        @JvmField val ShortObj = Java(java.lang.Short::class.java)
        @JvmField val FloatObj = Java(java.lang.Float::class.java)
        @JvmField val BooleanObj = Java(java.lang.Boolean::class.java)
        @JvmField val DoubleObj = Java(java.lang.Double::class.java)
        @JvmField val VoidObj = Java(java.lang.Void::class.java)

        inline fun <reified T: Any> of(): TypeInfo {
            return Java(T::class.java)
        }
    }
}

fun TypeInfo.asClassOfClass(): TypeInfo.Java {
    return TypeInfo.Java(recoverJClass()::class.java)
}

infix fun TypeInfo.sameAs(other: TypeInfo?): Boolean {
    return if (this is JClassAvailable && other is JClassAvailable) {
        jClass == other.jClass
    } else {
        getReturnStringRep() == other?.getReturnStringRep()
    }
}

interface MethodDescriptor {
    fun getReturnSignature(): String

    fun getArgsSWordArray(): Array<SWord>

    // return null if it is void
    fun getReturnSWord(): SWord?

    companion object {
        operator fun invoke(
            args: Array<out TypeInfo>,
            returns: TypeInfo
        ): MDInfo {
            return MDInfo(args, returns)
        }

        operator fun invoke(string: String): MDString {
            return MDString(string)
        }

        operator fun invoke(string: String, argsSWord: Array<SWord>?, returnSWord: SWord?): MDString {
            return MDString(string, argsSWord, returnSWord)
        }
    }
}

class MDString(
    @JvmField val stringRep: String,
    @JvmField val argsSWord: Array<SWord>?,
    @JvmField val returnSWord: SWord?
) : MethodDescriptor {
    constructor(stringRep: String) : this(stringRep, null, null)

    override fun getReturnSignature(): String {
        return stringRep
    }

    // TODO implement ability to find classes
    override fun getArgsSWordArray(): Array<SWord> {
        if (argsSWord != null) {
            return argsSWord
        }
        val desc = stringRep
        require(desc.startsWith("(") && desc.contains(")")) {
            "Invalid method descriptor: $desc"
        }

        val args = desc.substringAfter("(").substringBefore(")")
        if (args.isEmpty()) return emptyArray()

        val result = mutableListOf<SWord>()
        var i = 0
        while (i < args.length) {
            when (val c = args[i]) {
                'I', 'B', 'S', 'C', 'Z' -> {
                    result.add(SWord.I)
                    i++
                }
                'F' -> {
                    result.add(SWord.F)
                    i++
                }
                'J' -> {
                    result.add(SWord.L)
                    i++
                }
                'D' -> {
                    result.add(SWord.D)
                    i++
                }
                'L' -> {
                    val end = args.indexOf(';', i)
                    if (end == -1) throw IllegalArgumentException("Invalid descriptor: missing ; for reference type")
                    result.add(SWord.A)
                    i = end + 1
                }
                '[' -> {
                    while (args[i] == '[') i++ // Skip all array dimensions
                    if (args[i] == 'L') {
                        val end = args.indexOf(';', i)
                        if (end == -1) throw IllegalArgumentException("Invalid descriptor: missing ; in array reference type")
                        i = end + 1
                    } else {
                        i++
                    }
                    result.add(SWord.A)
                }
                else -> throw IllegalArgumentException("Unknown type in descriptor: $c")
            }
        }

        return result.toTypedArray()
    }

    override fun getReturnSWord(): SWord? {
        if (argsSWord != null) {
            return returnSWord
        }
        val ret = stringRep.substringAfter(")")
        return when (ret) {
            "V" -> null
            "I", "B", "S", "C", "Z" -> SWord.I
            "F" -> SWord.F
            "J" -> SWord.L
            "D" -> SWord.D
            else -> {
                if (ret.startsWith("L") || ret.startsWith("[")) {
                    SWord.A
                } else {
                    throw IllegalArgumentException("Unknown return type: $ret")
                }
            }
        }
    }
}

data class MDInfo(
    val args: Array<out TypeInfo>,
    val returns: TypeInfo
) : MethodDescriptor {
    fun firstIsCallTo(tp: TypeInfo): Boolean {
        val fa = args.firstOrNull() ?: return false
        return if (tp is JClassAvailable && fa is JClassAvailable) {
            fa.jClass == tp.jClass
        } else {
            fa.getStringRep() == tp.getStringRep()
        }
    }

    private fun TypeInfo.getSWord(): SWord {
        return if (this is TypeInfo.JClassAvailable) {
            when (jClass) {
                Double::class.java -> (D)
                Long::class.java -> (L)
                Int::class.java, Char::class.java, Boolean::class.java, Short::class.java -> (I)
                else -> (A)
            }
        } else {
            (A)
        }
    }

    override fun getArgsSWordArray(): Array<SWord> {
        return args.mapA {
            val k = it.getSWord()
            if (k === SWord.A) {
                val clRecovery = it.recoverJClass()
                SWord.A(clRecovery)
            } else {
                k
            }
        }
    }

    override fun getReturnSWord(): SWord? {
        return if (returns sameAs TypeInfo.Void) {
            null
        } else {
            val k=returns.getSWord()
            if (k === SWord.A) {
                val clRecovery = returns.recoverJClass()
                SWord.A(clRecovery)
            } else {
                k
            }
        }
    }

    companion object {
        fun fromFunction(fn: Function<*>): MethodDescriptor = fromFunction(fn.javaClass)
        fun fromFunction(fn: Class<Function<*>>): MethodDescriptor {
            val method = fn.methods.firstOrNull {
                it.name == "apply" || it.name.startsWith("invoke") // handles Kotlin & Java
            } ?: throw IllegalArgumentException("Cannot extract method from function: $fn")

            val paramTypes = method.parameterTypes.map {
                Java(it)
            }.toTypedArray()

            val returnType = Java(method.returnType)

            return MethodDescriptor(paramTypes, returnType)
        }

        @JvmField val EmptyWithVoid = MethodDescriptor(emptyArray(), TypeInfo.Void)
    }

    override fun getReturnSignature(): String {
        return buildString {
            append('(')
            for (arg in args) {
                append(arg.getReturnStringRep())
            }
            append(')')
            append(returns.getReturnStringRep())
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as MDInfo

        if (!args.contentEquals(other.args)) return false
        if (returns != other.returns) return false

        return true
    }

    override fun hashCode(): Int {
        var result = args.contentHashCode()
        result = 31 * result + returns.hashCode()
        return result
    }
}

inline fun <reified T: Any> boxed(): Class<T> = T::class.javaObjectType
inline fun <reified T: Any> java(): Class<T> = T::class.java

fun args(vararg arguments: Class<out Any>): MDInfo {
    return MethodDescriptor(args = arguments.mapA { TypeInfo.Java(it) }, TypeInfo.Void)
}

fun args(vararg arguments: KClass<out Any>): MDInfo {
    return MethodDescriptor(args = arguments.mapA { TypeInfo.Kt(it) }, TypeInfo.Void)
}

fun args(vararg arguments: TypeInfo): MDInfo {
    return MethodDescriptor(args = arguments, TypeInfo.Void)
}

fun args(): MDInfo {
    return MDInfo.EmptyWithVoid
}

infix fun MDInfo.returns(other: TypeInfo) = copy(returns = other)
infix fun MDInfo.returns(other: KClass<out Any>) = copy(returns = TypeInfo.Kt(other))

fun returns(other: KClass<out Any>) = MethodDescriptor(emptyArray(), TypeInfo.Kt(other))
fun returns(other: TypeInfo) = MethodDescriptor(emptyArray(), other)

fun TypeInfo.toASMType(): Type {
    return when (this) {
        is TypeInfo.JClassAvailable -> Type.getType(jClass)
        else -> error("Not available")
    }
}