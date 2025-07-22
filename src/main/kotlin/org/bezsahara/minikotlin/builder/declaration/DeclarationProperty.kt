package org.bezsahara.minikotlin.builder.declaration

import org.bezsahara.minikotlin.builder.Visibility
import kotlin.reflect.KClass

sealed interface DP {
    sealed interface CanBeField : DP
    sealed interface CanBeMethod : DP
    object Method : CanBeMethod
    object Field : CanBeField
    object Both : CanBeField, CanBeMethod
}

inline fun <reified T: Any> typeInfo(): TypeInfo.Java = TypeInfo.Java(T::class.javaObjectType)

inline fun <reified T: Any> typeInfoNt(): TypeInfo.Java = TypeInfo.Java(T::class.javaPrimitiveType ?: error("${T::class} cannot be primitive"))

fun Class<out Any>.toTypeString(): String {
    return this.packageName
}

inline fun <reified T: Function<*>> lambdaMethodDescription(): MethodDescriptor {
    return MDInfo.fromFunction(T::class.java as Class<Function<*>>)
}

data class DeclarationProperty<@Suppress("unused") T : DP, K : TypeHold>(
    val visibility: Visibility,
    val isFinal: Boolean = false,
    val isSynchronized: Boolean = false,
    val isStatic: Boolean = false,
    val isAbstract: Boolean = false,
    val isNative: Boolean = false,
    val isStrictfp: Boolean = false,
    val isDefault: Boolean = false,
    val isVolatile: Boolean = false,
    val isTransient: Boolean = false,
    val typeInfo: TypeInfo? = null,
    val annotations: List<String> = emptyList()
) {

    override fun toString(): String {
        return buildString {
            append(visibility.name.lowercase())
            append(' ')
            if (isFinal) append("final ")
            if (isSynchronized) append("synchronized ")
            if (isStatic) append("static ")
            if (isAbstract) append("abstract ")
            if (isNative) append("native ")
            if (isStrictfp) append("strictfp ")
            if (isDefault) append("default ")
            if (isVolatile) append("volatile ")
            if (isTransient) append("transient ")
            append(typeInfo!!.getStringRep())
            append(' ')
        }
    }

    fun
            <DPType : DP, KType : TypeHold>
            combine(other: DeclarationProperty<DPType, KType>): DeclarationProperty<DPType, KType>
    {
        return DeclarationProperty(
            visibility,
            isFinal || other.isFinal,
            isSynchronized || other.isSynchronized,
            isStatic || other.isStatic,
            isAbstract || other.isAbstract,
            isNative || other.isNative,
            isStrictfp || other.isStrictfp,
            isDefault || other.isDefault,
            isVolatile || other.isVolatile,
            isTransient || other.isTransient,
            other.typeInfo
        )
    }

    companion object {
        val public = DeclarationProperty<DP.Both, TypeHold.None>(Visibility.Public)
        val private = DeclarationProperty<DP.Both, TypeHold.None>(Visibility.Private)
        val protected = DeclarationProperty<DP.Both, TypeHold.None>(Visibility.Protected)

        val default = DeclarationProperty<DP.Method, TypeHold.None>(Visibility.None, isDefault = true)
        val final = DeclarationProperty<DP.Both, TypeHold.None>(Visibility.None, isFinal = true)
        val abstract = DeclarationProperty<DP.Method, TypeHold.None>(Visibility.None, isAbstract = true)
        val static = DeclarationProperty<DP.Both, TypeHold.None>(Visibility.None, isStatic = true)
        val synchronized = DeclarationProperty<DP.Method, TypeHold.None>(Visibility.None, isSynchronized = true)
        val native = DeclarationProperty<DP.Method, TypeHold.None>(Visibility.None, isNative = true)
        val strictfp = DeclarationProperty<DP.Method, TypeHold.None>(Visibility.None, isStrictfp = true)

        val volatile = DeclarationProperty<DP.Field, TypeHold.None>(Visibility.None, isVolatile = true)
        val transient = DeclarationProperty<DP.Field, TypeHold.None>(Visibility.None, isTransient = true)

        val voidType = DeclarationProperty<DP.Method, TypeInfo>(Visibility.None, typeInfo = TypeInfo.Void)

        val clintSig = DeclarationProperty<DP.Method, TypeInfo>(Visibility.None, isStatic = true, typeInfo = TypeInfo.Void)
    }
}

private fun <DPType : DP, T : TypeHold> DeclarationProperty<out DP, out TypeHold>
        .defaultCombine(other: DeclarationProperty<DPType, T>): DeclarationProperty<DPType, T> {
    return combine(other)
}

// Of type
fun ofType(typeInfo: TypeInfo) = DeclarationProperty<DP.Both, TypeInfo>(Visibility.None, typeInfo = typeInfo)
fun ofType(typeInfo: KClass<out Any>) = DeclarationProperty<DP.Both, TypeInfo>(Visibility.None, typeInfo = TypeInfo.Kt(typeInfo))

infix fun <DPType : DP, TInfo : TypeInfo>
        DeclarationProperty<DPType, TypeHold.None>
        .ofType(typeInfo: TInfo): DeclarationProperty<DPType, out TInfo> {
    return copy(typeInfo = typeInfo) as DeclarationProperty<DPType, TInfo>
}

infix fun <DPType : DP>
        DeclarationProperty<DPType, TypeHold.None>
        .ofType(typeInfo: KClass<out Any>): DeclarationProperty<DPType, out TypeInfo> {
    return ofType(TypeInfo.Kt(typeInfo))
}


// Semantics
infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .static(other: DeclarationProperty<DP.Both, T>): DeclarationProperty<DP.Both, T> {
    return defaultCombine(other).copy(isStatic = true)
}

infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .final(other: DeclarationProperty<DP.Both, T>): DeclarationProperty<DP.Both, T> {
    return defaultCombine(other).copy(isFinal = true)
}

infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .abstract(other: DeclarationProperty<out DP, T>): DeclarationProperty<DP.Method, T> {
    @Suppress("UNCHECKED_CAST") return defaultCombine(other).copy(isAbstract = true) as DeclarationProperty<DP.Method, T>
}

infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .synchronized(other: DeclarationProperty<out DP, T>): DeclarationProperty<DP.Method, T> {
    @Suppress("UNCHECKED_CAST") return defaultCombine(other).copy(isSynchronized = true) as DeclarationProperty<DP.Method, T>
}

infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .strictfp(other: DeclarationProperty<out DP, T>): DeclarationProperty<DP.Method, T> {
    @Suppress("UNCHECKED_CAST") return defaultCombine(other).copy(isStrictfp = true) as DeclarationProperty<DP.Method, T>
}


//variable modifiers
infix fun <T : TypeHold>
        DeclarationProperty<DP.Both, TypeHold.None>
        .volatile(other: DeclarationProperty<DP.Both, T>): DeclarationProperty<DP.Field, T> {
    @Suppress("UNCHECKED_CAST") return defaultCombine(other).copy(isVolatile = true) as DeclarationProperty<DP.Field, T>
}

infix fun <T : TypeInfo>
        DeclarationProperty<DP.Both, TypeHold.None>
        .transient(other: DeclarationProperty<DP.Both, T>): DeclarationProperty<DP.Field, T> {
    @Suppress("UNCHECKED_CAST") return defaultCombine(other).copy(isTransient = true) as DeclarationProperty<DP.Field, T>
}