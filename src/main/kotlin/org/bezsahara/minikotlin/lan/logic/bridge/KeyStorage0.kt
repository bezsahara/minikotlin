package org.bezsahara.minikotlin.lan.logic.bridge

import org.bezsahara.minikotlin.builder.KBKey
import java.util.concurrent.CopyOnWriteArrayList
import java.util.concurrent.atomic.AtomicInteger

object KeyStorage0 {
    private val keyStorage = CopyOnWriteArrayList<KBKey>()

    private val keyStorageIndex = AtomicInteger(0)

    @JvmStatic
    fun createNewKey(): Int {
        val newId = keyStorageIndex.getAndAdd(1)
        keyStorage.add(KBKey(newId))
        return newId
    }

    @JvmStatic
    fun getKBKey(a: Int): KBKey {
        return keyStorage[a]
    }

    @JvmStatic
    fun addToKBS(key: Int, v: Any?): Int {
        return keyStorage[key].autoSet(v)
    }

    @Suppress("unused")
    @JvmStatic
    fun getFromKBS(key: Int, v: Int): Any? { // TODO change to int
        return keyStorage[key][v]
    }
}
