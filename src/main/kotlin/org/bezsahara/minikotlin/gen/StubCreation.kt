package org.bezsahara.minikotlin.gen

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.InsnOp
import org.bezsahara.minikotlin.builder.opcodes.codes.MethodInsnOp
import org.bezsahara.minikotlin.builder.opcodes.codes.TypeInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode
import org.bezsahara.minikotlin.builder.opcodes.method.KBLdcOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBMethodCallOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBSingleOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBTypeOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.builder.opcodes.method.LabelPoint
import org.bezsahara.minikotlin.builder.opcodes.method.LocalVariableMetadata

object StubCreation {
    @JvmStatic
    fun createStubFor(kbResult: KBClass.Result): KBClass.Result {
        return kbResult.copy(
            methodsResult = kbResult.methodsResult.map {
                val newOps = it.operations.toMutableList()
                newOps.clear()
                addOperations(newOps, it.parameters)
                it.copy(operations = newOps)
            },
            classProperties = kbResult.classProperties.copy(verifier = false)
        )
    }

    private fun addOperations(list: MutableList<KBByteCode>, params: List<KBMethod.Parameter>) {
        val start = Label()
        val end = Label()
        list.add(LabelPoint(start))
        list.addAll(throwOps)
        list.add(LabelPoint(end))
        params.forEach {
            list.add(LocalVariableMetadata(it.name, it.typeInfo, start, end, it.index))
        }
    }

    private val geType = TypeInfo.Java(GenerationError::class.java)

    private val throwOps = listOf(
        KBTypeOP(TypeInsnOp.NEW, geType),
        KBSingleOP.createOrGet(InsnOp.DUP),
        KBLdcOP("Your program is trying to access a class that is not yet generated!", -1),
        KBMethodCallOP(
            MethodInsnOp.INVOKESPECIAL,
            geType,
            "<init>",
            MethodDescriptor(arrayOf(TypeInfo.String), TypeInfo.Void)
        ),
        KBSingleOP.createOrGet(InsnOp.ATHROW)
    )
}