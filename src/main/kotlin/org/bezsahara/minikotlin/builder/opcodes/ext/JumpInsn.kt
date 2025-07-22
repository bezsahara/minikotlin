@file:Suppress("SpellCheckingInspection", "FunctionName")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.codes.JumpInsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBJumpOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.builder.opcodes.method.LabelPoint

// === More descriptive aliases for jump instructions ===

fun KBMethod.jumpIfZero(label: Label) = ifeq(label)
fun KBMethod.jumpIfNotZero(label: Label) = ifne(label)
fun KBMethod.jumpIfLessThanZero(label: Label) = iflt(label)
fun KBMethod.jumpIfLessThanOrEqualToZero(label: Label) = ifle(label)
fun KBMethod.jumpIfGreaterThanOrEqualToZero(label: Label) = ifge(label)
fun KBMethod.jumpIfGreaterThanZero(label: Label) = ifgt(label)

fun KBMethod.jumpIfIntsEqual(label: Label) = if_icmpeq(label)
fun KBMethod.jumpIfIntsNotEqual(label: Label) = if_icmpne(label)
fun KBMethod.jumpIfIntLessThan(label: Label) = if_icmplt(label)
fun KBMethod.jumpIfIntGreaterOrEqual(label: Label) = if_icmpge(label)
fun KBMethod.jumpIfIntGreaterThan(label: Label) = if_icmpgt(label)
fun KBMethod.jumpIfIntLessOrEqual(label: Label) = if_icmple(label)

fun KBMethod.jumpIfRefsEqual(label: Label) = if_acmpeq(label)
fun KBMethod.jumpIfRefsNotEqual(label: Label) = if_acmpne(label)

fun KBMethod.jumpAlways(label: Label) = goto(label)
fun KBMethod.jumpToSubroutine(label: Label) = jsr(label) // Deprecated behavior

fun KBMethod.jumpIfNull(label: Label) = ifnull(label)
fun KBMethod.jumpIfNotNull(label: Label) = ifnonnull(label)

fun KBMethod.labelPoint(l: Label) {
    addOperation(LabelPoint(l))
}

/** Jump if value == 0 */
fun KBMethod.ifeq(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFEQ, label))
}

/** Jump if value != 0 */
fun KBMethod.ifne(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFNE, label))
}

/** Jump if value <= 0 */
fun KBMethod.ifle(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFLE, label))
}

/** Jump if value >= 0 */
fun KBMethod.ifge(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFGE, label))
}

/** Jump if value < 0 */
fun KBMethod.iflt(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFLT, label))
}

/** Jump if value > 0 */
fun KBMethod.ifgt(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFGT, label))
}

/** Jump if v2 == v1 (int) */
fun KBMethod.if_icmpeq(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPEQ, label))
}

/** Jump if v2 != v1 (int) */
fun KBMethod.if_icmpne(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPNE, label))
}

/** Jump if v2 < v1 (int) */
fun KBMethod.if_icmplt(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPLT, label))
}

/** Jump if v2 >= v1 (int) */
fun KBMethod.if_icmpge(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPGE, label))
}

/** Jump if v2 > v1 (int) */
fun KBMethod.if_icmpgt(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPGT, label))
}

/** Jump if v2 <= v1 (int) */
fun KBMethod.if_icmple(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ICMPLE, label))
}

/** Jump if v2 == v1 (ref) */
fun KBMethod.if_acmpeq(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ACMPEQ, label))
}

/** Jump if v2 != v1 (ref) */
fun KBMethod.if_acmpne(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IF_ACMPNE, label))
}

/** Unconditional jump */
fun KBMethod.goto(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.GOTO, label))
}

/** Jump to subroutine (deprecated) */
fun KBMethod.jsr(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.JSR, label))
}

/** Jump if ref == null */
fun KBMethod.ifnull(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFNULL, label))
}

/** Jump if ref != null */
fun KBMethod.ifnonnull(label: Label) {
    addOperation(KBJumpOP(JumpInsnOp.IFNONNULL, label))
}