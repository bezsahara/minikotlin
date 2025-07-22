package org.bezsahara.minikotlin.builder.auto

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.Visibility
import org.bezsahara.minikotlin.builder.declaration.DP
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.lan.logic.bridge.KeyStorage0

class KeyStoreInfo(
    val id: Int,
    val typeInfo: TypeInfo,
    val needsTypeCheck: Boolean
) {

    fun KBMethod.addToKB(kbClassId: Int) {
        ldcOptimized(kbClassId)
        ldcOptimized(id)
        invokestatic(TypeInfo.Kt(KeyStorage0::class), "getFromKBS", descriptor)
        if (needsTypeCheck) {
            checkcast(typeInfo)
        }
    }

    companion object {
        private val descriptor = MDString("(II)Ljava/lang/Object;", arrayOf(SWord.I, SWord.I), SWord.A)//args(Int::class, Int::class) returns Any::class
    }
}

fun insertAutoInitForKBS(kbMethod: KBMethod, kbClass: KBClass) {
    val dp = DeclarationProperty<DP.CanBeField, TypeInfo>(
        visibility = Visibility.Private,
        isStatic = true,
        isFinal = true
    )


    val c = kbMethod.capture {
        kbClass.keyNamesMap.forEach { (name, value) ->
            kbClass.run {
                dp.copy(typeInfo = value.typeInfo).field(name)
            }

            value.run {
                kbMethod.addToKB(kbClass.idOfKey)
            }
            kbMethod.putstatic(kbClass.ThisClass, name, value.typeInfo)

        }
    }

    if (c.startIndex == 0) {
        kbMethod.return_()
    } else {
        c.reposition(0, c.capturedOps.toList())
    }
}