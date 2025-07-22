@file:Suppress("UNCHECKED_CAST")

package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.lan.pieces.*
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap

class Condition

fun condition(): Condition {error("")}

// TODO make it all inline as well
class TableOrLookupSwitch(private val mk: MiniKotlin<*>, private val number: KRef.Native<Int>) {
    private val map = IntObjectHashMap<() -> Unit>()
    private var default: (() -> Unit)? = null

    fun case(i: Int, block: () -> Unit) {
        map.put(i, block)
    }
    fun default(block: () -> Unit) {
        default = block
    }
    fun execute() {
        val nonNullDefault = default ?: error("Default must be present!")

        val mapKeySetIterator = map.keySet().toArray()
        mapKeySetIterator.sort()
//        val mapValuesAndKeys = map.iterator()

        if (isItTable(mapKeySetIterator)) {
            val startId = mk.addPiece(SwitchPiece.TableStart(number, mapKeySetIterator))
            for (i in mapKeySetIterator) {
                val item = map[i]

                mk.addPiece(SwitchPiece.TableCaseStart(i, startId))
                item()
                mk.addPiece(SwitchPiece.TableCaseEnd(startId))
            }
            mk.addPiece(SwitchPiece.TableCaseDefaultStart(startId))
            nonNullDefault.invoke()
            mk.addPiece(SwitchPiece.TableCaseEnd(startId))
            mk.addPiece(SwitchPiece.TableEnd(startId))
        } else { // Lookup

            val startId = mk.addPiece(SwitchPiece.LookUpTableStart(number, mapKeySetIterator))
            for (i in mapKeySetIterator) {
                val item = map[i]

                mk.addPiece(SwitchPiece.LookUpTableCaseStart(i, startId))
                item()
                mk.addPiece(SwitchPiece.LookUpTableCaseEnd(startId))
            }
            mk.addPiece(SwitchPiece.LookUpTableCaseDefaultStart(startId))
            nonNullDefault.invoke()
            mk.addPiece(SwitchPiece.LookUpTableCaseEnd(startId))
            mk.addPiece(SwitchPiece.LookUpTableEnd(startId))
        }
    }

    private fun isItTable(mapKeySetIterator: IntArray): Boolean {
        if (mapKeySetIterator.size < 2) return true
        for (i in 1 until mapKeySetIterator.size) {
            if (mapKeySetIterator[i] - mapKeySetIterator[i - 1] != 1) return false
        }
        return true
    }
}

class Switch(val mk: MiniKotlin<*>) {
//    data class CPair(
//        val condition: KRef.Native<Boolean>,
//        val block: () -> Unit
//    )

//    private val cases = arrayListOf<CPair>()
//    private var elseBlock: (() -> Unit)? = null
    
    private val conditionBlocks = arrayListOf<ConditionBlockStart>()
    
    val conditionBlocksEmpty get() = conditionBlocks.isEmpty()
    
    fun addConditionBlock(cbs: ConditionBlockStart) {
        conditionBlocks.add(cbs)
    }

    private val st = ConditionStrictSpaceSTART(conditionBlocks)
    @JvmField val theStartId = mk.addPiece(st)

    fun markDefault() { st.containsDefault = true }
    
    fun makeScope(): Scope = Scope()
    
    inner class Scope() {
        inline fun case(condition: KRef.Native<Boolean>, block: () -> Unit) {
//            val id = mk.getIndex()
            val element = ConditionBlockStart(
                true, condition, theStartId
            )

            val id = mk.addPiece(element)
            // cases.add(CPair(condition, block))
            block()
            mk.addPiece(ConditionalBlockEnd(id, theStartId))
            addConditionBlock(element)
        }

//        require(ifBlock != null) { "If should have been defined if you want to use elseIf" }

        inline fun default(block: () -> Unit) {
            require(conditionBlocksEmpty) { "At least one case should have been defined if you want to use else" }

            val element = ConditionBlockStart(
                false, null, theStartId
            )
            val id = mk.addPiece(element)
            // elseBlock = block
            block()
            mk.addPiece(ConditionalBlockEnd(id, theStartId))
            addConditionBlock(element)
            markDefault()
        }
    }

    
    
    fun finish() {
        mk.addPiece(ConditionStrictSpaceEND(theStartId))
    }

    fun execute() {
//        val conditionsArray = arrayOfNulls<ConditionBlockStart>(
//            (if (elseBlock == null) 0 else 1) + cases.size
//        )
//        var caseIdx = 0
//        cases.forEach {
//            
//            it.block()
//            mk.addPiece(ConditionalBlockEnd(id, theStartId, mk.getIndex()))
//            conditionsArray[caseIdx] = element
//            caseIdx++
//        }
//        elseBlock?.let {
//            val id = mk.getIndex()
//            val element = ConditionBlockStart(
//                false, null, id, theStartId
//            )
//            mk.addPiece(element)
//            it()
//            mk.addPiece(ConditionalBlockEnd(id, theStartId, mk.getIndex()))
//            conditionsArray[conditionsArray.lastIndex] = element
//            st.containsDefault = true
//        }
//        mk.addPiece(ConditionStrictSpaceEND(theStartId))
    }
}