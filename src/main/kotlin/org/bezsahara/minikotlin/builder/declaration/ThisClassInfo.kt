package org.bezsahara.minikotlin.builder.declaration

import org.bezsahara.minikotlin.builder.auto.ClassUtils

class ThisClassInfo(val originalName: String, val shadowClass: Class<*>) : TypeInfo.JClassAvailable() {
    override val jClass: Class<out Any> = shadowClass

    override fun recoverJClass(): Class<*> {
        return shadowClass
    }

    override fun getStringRep(): String {
        return originalName
    }

    override fun getReturnStringRep(): String {
        return "L$originalName;"
    }

    companion object {
        fun withAutoShadow(fullName: String, interfaces: Array<String>? = null): ThisClassInfo {
            return ThisClassInfo(fullName, ClassUtils.generateEmptyClass("${fullName}Shadow", interfaces))
        }
    }
}