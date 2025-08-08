package org.bezsahara.minikotlin.builder.opcodes.method

import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.repr


class KBInvokeDyn(
    val name: String,
    val descriptor: MethodDescriptor,
    val bootstrapOwner: TypeInfo,
    val bootstrapName: String,
    val bootstrapDescriptor: MethodDescriptor,
    val isInterface: Boolean,
    val bootstrapArgs: Array<Any>
) : KBByteCode() {
    override fun toString(): String {
        return buildString {
            append("INVOKEDYNAMIC ").append(name).append(descriptor.getReturnSignature()).append(" [\n")
            append("  ").append(bootstrapOwner.getStringRep()).append('.').append(bootstrapName)
                .append(bootstrapDescriptor.getReturnSignature()).append('\n')

            append("  ").append("// arguments")
            bootstrapArgs.forEach {
                append("\n  ").append(repr(it.toString()))
            }
            append("\n]")
        }
    }

    override fun justInsnName(): String {
        return "INVOKEDYNAMIC"
    }

    override fun getBytesSize(currentOffset: Int): Int {
        TODO("Not yet implemented")
    }
}