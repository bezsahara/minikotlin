package org.bezsahara.minikotlin.builder.auto

import org.objectweb.asm.ClassWriter
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes.*

object ClassUtils {

    @JvmStatic
    fun generateEmptyClass(className: String, interfaces: Array<String>? = null): Class<*> {
        // ----- bytecode -----
        val cw = ClassWriter(0)
        cw.visit(V11, ACC_PUBLIC or ACC_SUPER, className, null, "java/lang/Object", interfaces)

        val mv: MethodVisitor = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null)
        mv.visitCode()
        mv.visitVarInsn(ALOAD, 0)
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V", false)
        mv.visitInsn(RETURN)
        mv.visitMaxs(1, 1)   // stack=1, locals=1
        mv.visitEnd()

        cw.visitEnd()
        val bytes = cw.toByteArray()

        // ----- loader -----
        return object : ClassLoader(this::class.java.classLoader) {
            fun define() = defineClass(className.replace("/", "."), bytes, 0, bytes.size)
        }.define()
    }
}
