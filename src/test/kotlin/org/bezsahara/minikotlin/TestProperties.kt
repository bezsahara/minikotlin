package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.KBClass
import java.io.File

object TestProperties {
    fun saveToFolder(r: KBClass.Result) {
        System.getProperty("compiled_folder")?.let {
            File(it).mkdirs()
            r.saveToFolder(it)
        }
    }
}

fun KBClass.Result.saveToTestFolderIfAny() {
    TestProperties.saveToFolder(this)
}