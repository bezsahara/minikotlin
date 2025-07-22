package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.opcodes.method.LabelPresent
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap
import java.util.*

class Frame(
    val before: List<SWordDebug>,
    variables: IntObjectHashMap<SWordDebug> // TODO implement variables checks
) {
    val variables = IntObjectHashMap<SWordDebug>(variables)

    val story = IdentityHashMap<LabelPresent, Unit?>()

    fun wasHereBefore(l: LabelPresent): Boolean {
        return story[l] != null
    }

    @Suppress("DuplicatedCode")
    fun isAcceptable(other: Frame): Boolean {
        // Fast-path: identical list reference → automatically equal.
        if (before === other.before) return true

        val debugData = variables.toArray().toList()
        val a = before
        val b = other.before
        val size = a.size
        if (size != b.size) return false          // different depth → incompatible

        // Check from top of stack (last element) downwards.
        for (i in size - 1 downTo 0) {
            if (!a[i].kind.compareByKind(b[i].kind)) return false       // mismatching slot → incompatible
        }
        confrontVariablesDV(other.variables)
        return true                               // all slots identical
    }

    // Good for debug
    private fun confrontVariablesDV(stored: IntObjectHashMap<SWordDebug>) {

        val vl = this.variables.keySet().toArray()

        for (key in vl) {
            val otherVal = stored.get(key)
            val thisVal = variables.get(key)

            val firstOne = otherVal == null

            if (firstOne) {
                this.variables.remove(key)
                continue
            }

            val secondOne = !thisVal.kind.compareByKind(otherVal.kind)

            if (secondOne) {
                this.variables.remove(key)
            }
        }

        // Now overwrite the canonical stored table
        stored.clear()
        stored.putAll(this.variables)
    }

    private fun confrontVariables(stored: IntObjectHashMap<SWordDebug>) {
        this.variables.forEachKeyValue { key, value ->
            val otherVal = stored.get(key)

//            if (otherVal == null || otherVal.kind !== value.kind) {
            if (otherVal == null || !value.kind.compareByKind(otherVal.kind)) {
                // Invalidate the slot in current path
//                this.variables.put(key, SWordDebug.TOP)
                this.variables.remove(key)
            }
        }

        // Now overwrite the canonical stored table
        stored.clear()
        stored.putAll(this.variables)
    }
}