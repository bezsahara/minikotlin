package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.ext.getstatic
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.ReusableRefObj
import org.bezsahara.minikotlin.lan.StackInfo
import org.bezsahara.minikotlin.lan.logic.bridge.KeyStorage0
import org.bezsahara.minikotlin.lan.pieces.CanExplainNull
import org.bezsahara.minikotlin.lan.pieces.NoCastNeeded


class OuterKey(val kbClass: KBClass, val value: Any?, intendedClass: Class<*>) : KValue.ValueBlockReturns(), NoCastNeeded, CanExplainNull {
    override fun isNullable(): Boolean {
        return value == null // TODO better change this
    }

    override fun toString(): String {
        return "ASD"
    }

    fun addToStorage(): String {
        val i = KeyStorage0.addToKBS(kbClass.idOfKey, value)
        return kbClass.insertNewEntryToMap(i, ftr)
    }

    private val ftr = TypeInfo.Java(intendedClass)

    companion object {
        private val descriptor = MDString("(II)Ljava/lang/Object;", arrayOf(SWord.I, SWord.I), SWord.A)//args(Int::class, Int::class) returns Any::class
    }

    private val nId  = addToStorage()

    override fun KBMethod.returns( // TODO optimize it later to static fields
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
//        ldcOptimized(kbClass.idOfKey)
//        ldcOptimized(nId)
//        invokestatic(TypeInfo.Kt(KeyStorage0::class), "getFromKBS", descriptor)
//        if (value != null) {
//            checkCast(ftr)
//        }
        getstatic(kbClass.ThisClass, nId, ftr)
    }

    override val objType: TypeInfo = ftr
}



// Optimize this later to static fields init
inline fun <reified T: Any> MiniKotlin<*>.immutableRefTo(o: T?): ReusableRefObj<T> {
    val java = T::class.java
    return ReusableRefObj(java, OuterKey(kbClass, o, java))
}