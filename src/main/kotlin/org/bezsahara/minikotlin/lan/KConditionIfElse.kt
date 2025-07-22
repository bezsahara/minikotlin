package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.lan.pieces.*

class IfBlock(val mk: MiniKotlinAny) {
    private val conditionBlocks = arrayListOf<ConditionBlockStart>()
    private val st = ConditionStrictSpaceSTART(conditionBlocks)
    @JvmField val theStartId = mk.addPiece(st)

    fun addConditionBlock(cbs: ConditionBlockStart) {
        conditionBlocks.add(cbs)
    }
    fun markDefault() { st.containsDefault = true }

    var chainEnded = false
        set(value) {
            if (value) {
                field = true
            } else {
                error("Cannot set it to false")
            }
        }

    fun finish() {
        chainEnded = true
        mk.addPiece(ConditionStrictSpaceEND(theStartId))
    }

    val markerKey = Any()

    inner class NextElseAdd() : MiniKotlin.BeforeNextAdd() {
        override fun wasDiscarded() {
            chainEnded = true
        }

        override fun getCodePieces(indexOfFirst: Int): List<ActionPiece> {
            return listOf(ConditionStrictSpaceEND(theStartId))
        }

        override fun isFineToAdd(nextPiece: ActionPiece): Boolean {
            return !(nextPiece is ConditionBlockStart && nextPiece.markerAny === markerKey)
        }
    }

    inline fun if_(condition: KRef.Native<Boolean>, block: () -> Unit): Else {
        val element = ConditionBlockStart(
            true, condition, theStartId
        )

        val id = mk.addPiece(element)
        block()
        mk.addPiece(ConditionalBlockEnd(id, theStartId))
        addConditionBlock(element)
        mk.beforeNextAddThis(NextElseAdd())

        return Else()
    }
//
//    inline fun if_(block: () -> Unit) {
//        return Else()
//    }

    inner class Else {
        inline infix fun else_(block: () -> Unit) {
            if (chainEnded) {
                error("You cannot add else_ now")
            }
//            val id = mk.getIndex()
            val element = ConditionBlockStart(
                false, null, theStartId, markerKey
            )
            val id = mk.addPiece(element)
            markDefault()
            block()
            mk.addPiece(ConditionalBlockEnd(id, theStartId))
            addConditionBlock(element)
            finish()
        }
    }
}