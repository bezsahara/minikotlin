package org.bezsahara.minikotlin.gen.detective

import org.objectweb.asm.*
import java.io.File
import java.io.InputStream
import java.nio.file.LinkOption
import java.nio.file.Path
import java.util.jar.JarFile
import kotlin.io.path.deleteExisting
import kotlin.io.path.forEachDirectoryEntry
import kotlin.io.path.isDirectory

fun clearDirectory(path: Path) {
    require(path.isDirectory(LinkOption.NOFOLLOW_LINKS))

    path.forEachDirectoryEntry {
        if (it.isDirectory(LinkOption.NOFOLLOW_LINKS)) {
            clearDirectory(it)
            it.deleteExisting()
        } else {
            it.deleteExisting()
        }
    }
}

fun findAnnotatedClasses(annotationDesc: String): List<String> {
    val result = mutableListOf<String>()
    val classPathEntries = System.getProperty("java.class.path")
        .split(File.pathSeparator)
        .map { File(it) }

    for (entry in classPathEntries) {
        if (entry.isDirectory) {
            entry.walkTopDown()
                .filter { it.isFile && it.extension == "class" }
                .forEach { file ->
                    file.inputStream().use { stream ->
                        processClassFile(stream, annotationDesc, result)
                    }
                }
        } else if (entry.isFile && entry.extension == "jar") {
            JarFile(entry).use { jar ->
                jar.entries().asSequence()
                    .filter { !it.isDirectory && it.name.endsWith(".class") }
                    .forEach { je ->
                        jar.getInputStream(je).use { stream ->
                            processClassFile(stream, annotationDesc, result)
                        }
                    }
            }
        }
    }

    return result
}

private fun processClassFile(stream: InputStream, annotationDesc: String, result: MutableList<String>) {
    val cr = ClassReader(stream)
    cr.accept(object : ClassVisitor(Opcodes.ASM9) {
        private var isInterface = false
        private var isAbstract = false
        private var className: String? = null
        private var annotated = false

        override fun visit(
            version: Int,
            access: Int,
            name: String,
            signature: String?,
            superName: String?,
            interfaces: Array<out String>?
        ) {
            className = name.replace('/', '.')
            isInterface = (access and Opcodes.ACC_INTERFACE) != 0
            isAbstract = (access and Opcodes.ACC_ABSTRACT) != 0
        }

        override fun visitAnnotation(descriptor: String, visible: Boolean): AnnotationVisitor? {
            if (descriptor == annotationDesc) {
                annotated = true
            }
            return super.visitAnnotation(descriptor, visible)
        }

        override fun visitEnd() {
            val cn = className ?: return
            if (annotated &&
                !isInterface &&
                !isAbstract &&
                !cn.contains('$')
            ) {
                result.add(cn)
            }
        }
    }, ClassReader.SKIP_CODE or ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
}