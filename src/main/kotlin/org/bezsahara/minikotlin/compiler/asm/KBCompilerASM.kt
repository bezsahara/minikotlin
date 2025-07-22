package org.bezsahara.minikotlin.compiler.asm

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.KBField
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.getASMOpcodes
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.compiler.KBCompiler
import org.objectweb.asm.*
import org.objectweb.asm.util.ASMifier
import org.objectweb.asm.util.TraceClassVisitor
import java.io.File
import java.io.PrintWriter
import java.util.*

class KBCompilerASM(
    override val version: Int = Opcodes.V23
) : KBCompiler {

    override fun compileClass(kbClassResult: KBClass.Result): ByteArray {
        val methods = kbClassResult.methodsResult
        val fields =  kbClassResult.fields
        val cw = ClassWriter(ClassWriter.COMPUTE_FRAMES)

        val classWriter = processClass(kbClassResult, cw)
        compileFields(classWriter, fields)
        compileMethods(classWriter, methods)
        classWriter.visitEnd()
        return cw.toByteArray()
    }

    val labelMap = IdentityHashMap<Label, org.objectweb.asm.Label>()

    private fun compileFields(classWriter: ClassVisitor, fields: List<KBField>) {
        for (field in fields) {
            classWriter.visitField(
                field.declarationProperty.getASMOpcodes(),
                field.name,
                field.declarationProperty.typeInfo!!.getReturnStringRep(),
                null,
                field.value
            )
        }
    }

    private fun compileMethods(classWriter: ClassVisitor, methods: List<KBMethod.Result>) {
//        classWriter.visitSource(null, null)
        for (method in methods) {
            val mv = classWriter.visitMethod(
                method.methodProperty.getASMOpcodes(),
                method.name,
                MethodDescriptor(
                    args = method.parameters.mapA { it.typeInfo },
                    returns = method.methodProperty.typeInfo!!
                ).getReturnSignature(),
                null,
                emptyArray()
            )
            mv.visitCode()
            compileMethod(mv, method)
//            mv.visitLineNumber()
            mv.visitMaxs(0,0)
            mv.visitEnd()
        }
    }

    private fun processClass(kbClassResult: KBClass.Result, cw: ClassWriter): ClassVisitor {
        val classWriter = if (false) {
            TraceClassVisitor(
                cw,
                ASMifier().apply { print(PrintWriter(System.out)) },
                PrintWriter(System.out)
            )
        } else cw

        classWriter.visit(
            version,
            Opcodes.ACC_PUBLIC or Opcodes.ACC_FINAL or Opcodes.ACC_SUPER,
            kbClassResult.name,
            null,
            "java/lang/Object",
            kbClassResult.interfaces.mapA {
                it.getStringRep()
            }
        )
        return classWriter
    }
}

fun stripLineNumbers(inputClassPath: String, outputClassPath: String) {
    val classReader = ClassReader(inputClassPath)

    val classWriter = ClassWriter(classReader, 0)

    val filteringVisitor = object : ClassVisitor(Opcodes.ASM9, classWriter) {
        override fun visitMethod(
            access: Int,
            name: String?,
            descriptor: String?,
            signature: String?,
            exceptions: Array<out String?>?
        ): MethodVisitor {
            val base = super.visitMethod(access, name, descriptor, signature, exceptions)
            return object : MethodVisitor(Opcodes.ASM9, base) {
                override fun visitLineNumber(line: Int, start: org.objectweb.asm.Label) {
                    // Do nothing, skip the line number entirely
                }
            }
        }
    }

    classReader.accept(filteringVisitor, 0)

    File(outputClassPath).writeBytes(classWriter.toByteArray())
}