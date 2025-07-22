package org.bezsahara.minikotlin.builder

// TODO add ability to verify that class is accessing its own storage
//  also need an ability to remove the whole thing
class KBKey(val innerId: Int) {
    private var innerList: ArrayList<Any?>? = ArrayList()
    private var idxFree = 0

    private var builtArray: Array<Any?> = emptyArray()

    operator fun get(i: Int): Any? {
        return builtArray[i]
    }

    fun build() {
        innerList?.let {
            builtArray = it.toArray()
            innerList = null
        }
    }

    fun autoSet(v: Any?): Int {
        innerList?.add(v) ?: error("InnerList was closed already")
        return idxFree++
    }
}
