package org.bezsahara.minikotlin.builder

import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.MDInfo
import org.bezsahara.minikotlin.compiler.asm.mapA

typealias ThisMethodAny = ThisMethod<out Any>

class ThisMethod<T: Any>(
    val name: String,
    parameters: List<KBMethod.Parameter>,
    val methodDeclarationProperty: DeclarationProperty<*, *>,
) {
    // just in case
    val parameters = parameters.sortedBy { it.index }


    fun methodDescriptor(): MDInfo {
        return MDInfo(parameters.mapA { it.typeInfo }, methodDeclarationProperty.typeInfo!!)
    }
}