package org.bezsahara.minikotlin.builder.opcodes.method

import org.bezsahara.minikotlin.builder.declaration.TypeInfo

class LocalVariableMetadata(
    val name: String,
    val typeDesc: TypeInfo,
    val start: Label,
    val end: Label,
    val index: Int
) : ByteCodeMetaData() {
    init {
        start.markAsMeta()
        end.markAsMeta()
    }
}
