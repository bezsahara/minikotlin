package org.bezsahara.minikotlin.lan.pieces

import org.bezsahara.minikotlin.lan.KVar

sealed interface ThrowPiece : ActionPiece {
    class TryStart() : ThrowPiece

    class TryEnd(val tryStartId: Int) : ThrowPiece

    class CatchStart(val tryStartId: Int, val errorType: Class<*>, val catchVariable: KVar<*>?) : ThrowPiece

    class CatchEnd(val tryStartId: Int) : ThrowPiece
}