package org.bezsahara.minikotlin.lan.helper

import org.bezsahara.minikotlin.lan.pieces.ActionPiece
import org.bezsahara.minikotlin.lan.pieces.RevealId

class CodePiecesMap(data: List<ActionPiece>) {
    //.sortedBy { it.id }
    val array = data.setIDsIfAny().toTypedArray()

    private fun List<ActionPiece>.setIDsIfAny(): List<ActionPiece> {
        forEachIndexed { index, piece ->
            if (piece is RevealId) {
                piece.id = index
            }
        }
//        forEachIndexed { index, piece ->
//            if (piece is RevealId) {
//                print("(${piece.id})")
//            }
//            println("id $index: $piece")
//        }
        return this
    }

    operator fun get(int: Int): ActionPiece {
        return array[int]
    }
}