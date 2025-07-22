package org.bezsahara.minikotlin.builder

import org.bezsahara.minikotlin.builder.declaration.TypeInfo

class ThisField(
    val name: String,
    val typeInfo: TypeInfo,
    val isStatic: Boolean
)