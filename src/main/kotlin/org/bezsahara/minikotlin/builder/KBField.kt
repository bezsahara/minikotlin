package org.bezsahara.minikotlin.builder

import org.bezsahara.minikotlin.builder.declaration.DP
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.TypeInfo

class KBField(
    val name: String,
    val declarationProperty: DeclarationProperty<out DP, out TypeInfo>
) {
    var value: Any? = null

    infix fun valueOf(v: Any) {
        value = v
    }
}