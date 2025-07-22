package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.method.KBVariableOP
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap

object VariableProblem {

//    const val CHECK_STORE_TOP = 0
//    const val CHECK_STORE_NOT_FOUND = 1
    const val CHECK_STORE_OCCUPIES_W64 = 1
    const val CHECK_STORE_FINE = 2

    fun checkStore(varOp: KBVariableOP, map: IntObjectHashMap<SWordDebug>): Int {
//        val kind = varOp.instruction.stackTaken

        val value = map[varOp.variableIndex]
        if (value == null) {

            val doubleOrLongTry = map[varOp.variableIndex - 1]
            if (doubleOrLongTry != null && doubleOrLongTry.kind is SWord.W64) {
                return CHECK_STORE_OCCUPIES_W64
            }
            return CHECK_STORE_FINE
        }

//        if (value === SWordDebug.TOP) {
//            return CHECK_STORE_TOP
//        }



        return CHECK_STORE_FINE
    }

    const val CHECK_LOAD_NOT_FOUND = 1
    const val CHECK_LOAD_WRONG_KIND = 2
    const val CHECK_LOAD_FINE = 3

    fun checkLoad(varOp: KBVariableOP, map: IntObjectHashMap<SWordDebug>): Int {

        val value = map[varOp.variableIndex]

        if (value == null) {
            return CHECK_LOAD_NOT_FOUND
        }

        if (!varOp.instruction.stackGiven!!.canAccept(value.kind)) {
            return CHECK_LOAD_WRONG_KIND
        }

        return CHECK_LOAD_FINE
    }
}