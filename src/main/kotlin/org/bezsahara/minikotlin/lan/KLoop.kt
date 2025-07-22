package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.lan.pieces.WhileLoopBreak
import org.bezsahara.minikotlin.lan.pieces.WhileLoopPieceEnd
import org.bezsahara.minikotlin.lan.pieces.WhileLoopPieceStart

class WhileLoop(val mk: MiniKotlin<*>, condition: KRef.Native<Boolean>, whileFirst: Boolean) {
    private val loopIdStart = mk.addPiece(WhileLoopPieceStart(condition, whileFirst))


    inner class Scope {
        @Suppress("FunctionName")
        fun break_() {
            mk.addPiece(WhileLoopBreak(loopIdStart))
        }
    }

    fun end() {
        mk.addPiece(WhileLoopPieceEnd(loopIdStart))
    }
}