package org.bezsahara.minikotlin.lan.lib.kinds

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.ext.getstatic
import org.bezsahara.minikotlin.builder.opcodes.ext.invokevirtual
import org.bezsahara.minikotlin.builder.opcodes.ext.ldc
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

inline fun <reified T: Any> KRef.Obj<T>.getClass(): KRef.Obj<Class<T>> {
    return KRef.Obj(Class::class.java as Class<Class<T>>, GetClassValue(this))
}

//INVOKEVIRTUAL java/lang/Object.getClass ()Ljava/lang/Class;
class GetClassValue(ref: KRef<*>) : KValue.ValueBlockReturns(arrayOf(ref)) {
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        invokevirtual(TypeInfo.Object, "getClass", dp)
    }

    override val objType: TypeInfo = TypeInfo.Class

    companion object {
        private val dp = MDString("()Ljava/lang/Class;", emptyArray(), SWord.A)
    }
}

class MiniClassValue(val c: Class<*>, val primitive: Boolean): KValue.ValueBlockReturns() {
    override val objType: TypeInfo = TypeInfo.Java(c::class.java)
    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        if (!primitive) {
            ldc(TypeInfo.Java(c))
        } else { // GETSTATIC java/lang/Integer.TYPE : Ljava/lang/Class;
            when (c) {
                Int::class.java -> getstatic(TypeInfo.IntObj, "TYPE", TypeInfo.Class)
                Boolean::class.java -> getstatic(TypeInfo.BooleanObj, "TYPE", TypeInfo.Class)
                Byte::class.java -> getstatic(TypeInfo.ByteObj, "TYPE", TypeInfo.Class)
                Char::class.java -> getstatic(TypeInfo.CharObj, "TYPE", TypeInfo.Class)
                Short::class.java -> getstatic(TypeInfo.ShortObj, "TYPE", TypeInfo.Class)
                Long::class.java -> getstatic(TypeInfo.LongObj, "TYPE", TypeInfo.Class)
                Float::class.java -> getstatic(TypeInfo.FloatObj, "TYPE", TypeInfo.Class)
                Double::class.java -> getstatic(TypeInfo.DoubleObj, "TYPE", TypeInfo.Class)
                Void::class.java -> getstatic(TypeInfo.VoidObj, "TYPE", TypeInfo.Class)
                else -> error("")
            }
        }
    }
}

fun <T: Any> miniClass(c: Class<T>): KRef.Obj<Class<T>> {
    return KRef.Obj(c::class.java as Class<Class<T>>, MiniClassValue(c, c.isPrimitive))
}

inline fun <reified T: Any> miniClassNtOf(): KRef.Obj<Class<T>> {
    return miniClass(T::class.javaPrimitiveType ?: error("Type ${T::class.java} is not native type"))
}

inline fun <reified T: Any> miniClassOf(): KRef.Obj<Class<T>> {
    return miniClass(T::class.javaObjectType)
}
