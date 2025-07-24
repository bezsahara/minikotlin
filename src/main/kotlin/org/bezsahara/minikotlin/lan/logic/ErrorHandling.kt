package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.lan.KVar
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.MiniKotlinAny
import org.bezsahara.minikotlin.lan.pieces.ActionPiece
import org.bezsahara.minikotlin.lan.pieces.ThrowPiece
import kotlin.reflect.KClass

fun sdd() {
    try {
        print("")
    } catch (e: Throwable) {
        print(e)
    }
}

class ErrorHandler(val mk: MiniKotlinAny) {

    private val tryStartId = mk.addPiece(ThrowPiece.TryStart())

    fun endOfTry(): CatchBlock {
        mk.addPiece(ThrowPiece.TryEnd(tryStartId))

        mk.beforeNextAddThis(TryCatchBeforeNextAdd())
        return CatchBlock()
    }

    fun startOfCatch(errType: Class<*>): KVar.Obj<*> {
        val variable = mk.variable(mk.createVariableName(), errType.kotlin)
        variable.initialized = true
        mk.addPiece(ThrowPiece.CatchStart(tryStartId, errType, variable))
        return variable
    }

    fun endOfCatch() {
        mk.addPiece(ThrowPiece.CatchEnd(tryStartId))
    }

    var wasDiscarded = false
        private set

    inner class TryCatchBeforeNextAdd() : MiniKotlin.BeforeNextAdd() {
        override fun wasDiscarded() {
            wasDiscarded = true
        }

        override fun getCodePieces(indexOfFirst: Int): List<ActionPiece> {
            return listOf(ThrowPiece.CatchStart(tryStartId, Throwable::class.java, null), ThrowPiece.CatchEnd(tryStartId))
        }

        override fun isFineToAdd(nextPiece: ActionPiece): Boolean {
            return !(nextPiece is ThrowPiece.CatchStart && nextPiece.tryStartId == tryStartId)
        }

    }

    inner class CatchBlock() {
        @Suppress("UNCHECKED_CAST")
        inline fun <T: Throwable> catch(errorType: KClass<T>, block: (KVar.Obj<T>) -> Unit) {
            if (wasDiscarded) error("Cannot add catch block here!")
            val v = startOfCatch(errorType.java)
            block(v as KVar.Obj<T>)
            endOfCatch()
        }

        @Suppress("UNCHECKED_CAST")
        inline fun <reified T: Throwable> catch(block: (KVar.Obj<T>) -> Unit) {
            catch(T::class, block)
        }

        @Suppress("UNCHECKED_CAST")
        inline fun catchAny(block: (KVar.Obj<Throwable>) -> Unit) {
            if (wasDiscarded) error("Cannot add catch block here!")
            val v = startOfCatch(Throwable::class.java)
            block(v as KVar.Obj<Throwable>)
            endOfCatch()
        }
    }
}