package org.bezsahara.minikotlin

import org.bezsahara.minikotlin.builder.KBClass

object TestProperties {
    fun saveToFolder(r: KBClass.Result) {
        System.getProperty("compiled_folder")?.let {
            r.saveToFolder(it)
        }
    }
}

fun KBClass.Result.saveToTestFolderIfAny() {
    TestProperties.saveToFolder(this)
}