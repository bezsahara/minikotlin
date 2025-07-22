@file:Suppress("DuplicatedCode")

package org.bezsahara.minikotlin

import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

fun compress(input: String): ByteArray {
    val bos = ByteArrayOutputStream()
    GZIPOutputStream(bos).use { it.write(input.toByteArray(Charsets.UTF_8)) }
    return bos.toByteArray()
}
