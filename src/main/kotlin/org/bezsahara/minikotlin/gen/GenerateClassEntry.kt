package org.bezsahara.minikotlin.gen

import org.bezsahara.minikotlin.gen.detective.clearDirectory
import org.bezsahara.minikotlin.gen.detective.findAnnotatedClasses
import kotlin.io.path.Path
import kotlin.io.path.createDirectory
import kotlin.io.path.notExists
import kotlin.io.path.writeBytes

object GenerateClassEntry {
    @JvmStatic
    fun main(args: Array<String>?) {
        val genDir = Path(System.getProperty("minikotlin.genDir"))
        val stubDir = Path(System.getProperty("minikotlin.stubDir"))
        clearDirectory(genDir)
        clearDirectory(stubDir)
        val classNames = findAnnotatedClasses("Lorg/bezsahara/minikotlin/annotations/MiniKotlinGenerator;")

        classNames.forEach { className ->
            val clazz = Class.forName(className)
            val cons = clazz.constructors.firstOrNull { it.parameters.size == 0 }
                ?: error("Classes annotated with MiniKotlinGenerator must have at least one no-parameter constructor! $className")

            val instance = cons.newInstance()
            if (instance is MKGenerator) {
                instance.generateClass().forEach { kbResult ->

                    val kbStubResult = StubCreation.createStubFor(kbResult)

                    val packageVector = kbResult.name.split("/")

                    var genCurrent = genDir
                    var stubCurrent = stubDir
                    packageVector.forEachIndexed { index, string ->
                        if (index == packageVector.lastIndex) {
                            genCurrent.resolve("$string.class").writeBytes(kbResult.toByteArray())
                            stubCurrent.resolve("$string.class").writeBytes(kbStubResult.toByteArray())
                        } else {
                            genCurrent = genCurrent.resolve(string)
                            stubCurrent = stubCurrent.resolve(string)
                            if (genCurrent.notExists()) {
                                genCurrent.createDirectory()
                            }
                            if (stubCurrent.notExists()) {
                                stubCurrent.createDirectory()
                            }
                        }
                    }
                }

            } else {
                error("Classes annotated with MiniKotlinGenerator must implement MKGenerator. $className")
            }
        }
    }
}