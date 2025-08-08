package org.bezsahara.minikotlin.gradle

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.plugins.JavaPluginExtension
import org.gradle.api.tasks.JavaExec
import org.gradle.kotlin.dsl.register

class MiniKotlinPlugin : Plugin<Project> {
    override fun apply(project: Project) {
        val ext = project.extensions.create(
            "minikotlin",
            MiniKotlinExtension::class.java,
            project.objects
        )


        project.dependencies.add("compileOnly", project.files(ext.stubFolder))
//        project.dependencies.add("runtimeOnly", project.files(ext.generateFolder))

        project.extensions.getByType(JavaPluginExtension::class.java)
            .sourceSets.getByName("main") {
//                resources.srcDir(ext.stubFolder)
                output.dir(ext.generateFolder)
            }

        val configureMiniKotlin = project.tasks.register("configureMiniKotlin") {
            group = "minikotlin"
            description = "Ensures stub and generate folders exist"
            doLast {
                ext.stubFolder.get().asFile.mkdirs()
                ext.generateFolder.get().asFile.mkdirs()
            }
        }

        project.tasks.register<JavaExec>("generateMiniKotlin") {
            group = "minikotlin"
            description = "Generates all the classes"

            dependsOn(configureMiniKotlin)
            dependsOn("classes")

            val ext = project.extensions.getByType(MiniKotlinExtension::class.java)
            val sourceSets = project.extensions.getByType(JavaPluginExtension::class.java).sourceSets
            val mainSourceSet = sourceSets.getByName("main")

            // Extend runtimeClasspath just for this execution
            classpath = mainSourceSet.runtimeClasspath + project.files(ext.stubFolder)

            mainClass.set(GENERATE_CLASS_PATH)

            doFirst {
                jvmArgs(
                    "-Dminikotlin.genDir=${ext.generateFolder.get().asFile}",
                    "-Dminikotlin.stubDir=${ext.stubFolder.get().asFile}"
                )
            }
        }
        project.afterEvaluate {
            when (ext.runMode.get()) {
                MiniKotlinRunMode.AUTO -> {
                    tasks.named("classes") {
                        finalizedBy(tasks.named("generateMiniKotlin"))
                    }
                }
                else -> { // MiniKotlinRunMode.MANUAL -> {
                    // do nothing, user wires manually
                }
            }
        }
    }

    companion object {
        const val GENERATE_CLASS_PATH = "org.bezsahara.minikotlin.gen.GenerateClassEntry"
    }
}