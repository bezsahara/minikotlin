package org.bezsahara.minikotlin.compiler.asm

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.method.*
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Label as ASMLabel


fun rasp(a: Boolean) {
    if (a) {
        val hello = 0
        println(hello)
    } else {
        val goodbye = "12"
        println(goodbye)
    }
}

fun KBCompilerASM.compileMethod(methodVisitor: MethodVisitor, method: KBMethod.Result) {

    for (operationNf in method.operations) {
        val operation = if (operationNf is KBByteCode.Debug) {
            operationNf.value
        } else {
            operationNf
        }
        when (operation) {
            is KBFieldOP -> {
                methodVisitor.visitFieldInsn(
                    operation.instruction.opcode,
                    operation.owner.getStringRep(),
                    operation.name,
                    operation.descriptor.getReturnStringRep()
                )
            }

            is KBIincOP -> {
                methodVisitor.visitIincInsn(operation.variableIndex, operation.increment)
            }

            is KBLdcOP -> {
                methodVisitor.visitLdcInsn(operation.getASMValue())
            }

            is KBLookupSwitchOP -> {
                methodVisitor.visitLookupSwitchInsn(
                    labelMap.getOrPut(operation.default) { ASMLabel() },
                    operation.keys,
                    operation.cases.mapA { labelMap.getOrPut(it) { ASMLabel() } }
                )
            }

            is KBMethodCallOP -> {
                methodVisitor.visitMethodInsn(
                    operation.instruction.opcode,
                    operation.owner.getStringRep(),
                    operation.name,
                    operation.descriptor.getReturnSignature(),//.getByteCodeMD(),
                    operation.isInterface
                )
            }

            is KBSingleOP -> {
                methodVisitor.visitInsn(operation.instruction.opcode)
            }

            is KBTableSwitchOP -> {
                methodVisitor.visitTableSwitchInsn(
                    operation.min,
                    operation.max,
                    labelMap.getOrPut(operation.default) { ASMLabel() },
                    *operation.cases.mapA { labelMap.getOrPut(it) { ASMLabel() } }
                )
            }

            is KBVariableOP -> {
                methodVisitor.visitVarInsn(operation.instruction.opcode, operation.variableIndex)
            }

            is KBAsmOp -> operation.apply { methodVisitor.visit() }
            is KBJumpOP -> methodVisitor.visitJumpInsn(
                operation.instruction.opcode,
                labelMap.getOrPut(operation.label) { ASMLabel() })

            is KBTypeOP -> {
                methodVisitor.visitTypeInsn(operation.instruction.opcode, operation.typeInfo.getStringRep())
            }

            is KBSingleIntOP -> methodVisitor.visitIntInsn(operation.instruction.opcode, operation.operand)
            is KBTryCatchBlockOP -> methodVisitor.visitTryCatchBlock(
                labelMap.getOrPut(operation.startTry) { ASMLabel() },
                labelMap.getOrPut(operation.endTry) { ASMLabel() },
                labelMap.getOrPut(operation.startCatch) { ASMLabel() },
                operation.exceptionType.getStringRep()
            )

            is KBByteCode.Debug -> error("Not expected to see Debug here")
            is LabelPoint -> {
                val label = labelMap.getOrPut(operation.label) { ASMLabel() }
                methodVisitor.visitLabel(label)
            }

            is DebugStack -> Unit
            is JustPrint -> Unit // Ignore
            is LocalVariableMetadata -> methodVisitor.visitLocalVariable(
                operation.name,
                operation.typeDesc.getReturnStringRep(),
                null,
                labelMap.getOrPut(operation.start) { ASMLabel() },
                labelMap.getOrPut(operation.end) { ASMLabel() },
                operation.index
            )
        }
    }
}

//methodVisitor.visitLocalVariable("goodbye", "Ljava/lang/Integer;", null, label1, label2, 1);
//methodVisitor.visitLocalVariable("hello", "I", null, label0, label1, 1);

fun KBLdcOP.retrieveAppropriateValue(): Any {
    return value
}

inline fun <T, reified R> Array<T>.mapA(block: (T) -> R): Array<R> {
    val newArray = arrayOfNulls<R>(size)
    for (i in indices) {
        newArray[i] = block(get(i))
    }
    return newArray as Array<R>
}

inline fun <T, reified R> List<T>.mapA(block: (T) -> R): Array<R> {
    val newArray = arrayOfNulls<R>(size)
    for (i in indices) {
        newArray[i] = block(get(i))
    }
    return newArray as Array<R>
}