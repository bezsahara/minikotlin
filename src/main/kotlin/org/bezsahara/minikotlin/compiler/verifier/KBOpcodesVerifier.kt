package org.bezsahara.minikotlin.compiler.verifier

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.ThisClassInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo.KArray
import org.bezsahara.minikotlin.builder.declaration.asClassOfClass
import org.bezsahara.minikotlin.builder.opcodes.codes.*
import org.bezsahara.minikotlin.builder.opcodes.codes.FieldInsnOp.Companion.GETFIELD
import org.bezsahara.minikotlin.builder.opcodes.codes.FieldInsnOp.Companion.GETSTATIC
import org.bezsahara.minikotlin.builder.opcodes.codes.FieldInsnOp.Companion.PUTFIELD
import org.bezsahara.minikotlin.builder.opcodes.codes.FieldInsnOp.Companion.PUTSTATIC
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord.*
import org.bezsahara.minikotlin.builder.opcodes.method.*
import org.bezsahara.minikotlin.compiler.asm.mapA
import org.bezsahara.minikotlin.compiler.verifier.v.VerifierVariableUninitializedException
import org.bezsahara.minikotlin.compiler.verifier.v.VerifierVariableWrongKindException
import org.eclipse.collections.impl.map.mutable.primitive.IntObjectHashMap
import java.util.IdentityHashMap
import kotlin.collections.ArrayDeque
import kotlin.collections.getOrNull


// Probably a better solution was to build blocks of paths first and then check them
// But, it would have been probably slower, actually I have no idea. If anyone wants to write better verifier do it
class KBOpcodesVerifier(
    classProperties: ClassProperties,
    private val allByteCode: List<KBByteCode>,
    private val returnType: Class<*>,
    private val parameters: List<KBMethod.Parameter>?,
    private val isStatic: Boolean,
    private val functionName: String,
    private val thisClassInfo: ThisClassInfo
) {

    companion object {
        const val PEEP_FAIL = 0//0
        const val PEEP_FAIL_2SLOT = 1//2
        const val PEEP_SUCCESS = 2//1
        const val PEEP_SUCCESS_2SLOT = 3//12

    }

    private val shouldTrackVariables = classProperties.trackVariables
    private val cSSize = allByteCode.size

    private fun ArrayDeque<SWordDebug>.push(o: SWord, origin: KBByteCode) {
        addLast(SWordDebug(o, origin))
    }

    private fun ArrayDeque<SWordDebug>.push(sWordDebug: SWordDebug) {
        addLast(sWordDebug)
    }

    private fun ArrayDeque<SWordDebug>.pop(globalIndex: Int): SWordDebug {
        return removeLastOrNull() ?: error("The stack is empty")
    }

    private fun ArrayDeque<SWordDebug>.popOrNull(): SWordDebug? {
//        debugDeque.removeLastOrNull()
        return removeLastOrNull()
    }

    private fun ArrayDeque<SWordDebug>.peepCompare(c: SWord): Int { // 0 - fail, 2 - fail with 2 slots - 1 success, 12 - success at removing two
        return when (c) {
            V, V1 -> error("")
            W32 -> {
                if (getOrNull(size - 1)?.kind is W32) PEEP_SUCCESS else PEEP_FAIL
            }

            W64 -> {
                if (getOrNull(size - 1)?.kind is W64) PEEP_SUCCESS else PEEP_FAIL
            }

            is W32, is W64 -> {
                val getAttempt = getOrNull(size - 1)
                if (getAttempt == null) {
                    PEEP_FAIL
                } else {
                    if (c.canAccept(getAttempt.kind)) {
                        PEEP_SUCCESS
                    } else {
                        PEEP_FAIL
                    }
                }
            }
            W64Both -> {
                when (getOrNull(size - 1)?.kind) {
                    V, V1 -> error("")

                    is W32 -> {
                        if (getOrNull(size - 2)?.kind is W32) PEEP_SUCCESS_2SLOT else PEEP_FAIL_2SLOT
                    }

                    is W64, W64Both -> PEEP_SUCCESS
                    null -> PEEP_FAIL
                }
            }
        }
    }

    private fun SWord.popCompareSafely(
        stack: ArrayDeque<SWordDebug>,
        byteCode: KBByteCode,
        currentIndex: Int
    ) {
        popCompareSafely(stack, byteCode, null, currentIndex)
    }

    private fun SWord.popCompareSafely(
        stack: ArrayDeque<SWordDebug>,
        byteCode: KBByteCode,
        removedBuffer: Array<SWordDebug>?,
        currentIndex: Int,
    ) {
        when (stack.peepCompare(this)) {
            PEEP_FAIL -> {
//                val stackPop = stack.popOrNull()
                produceError(
                    byteCode,
                    stack.toTypedArray(),
                    arrayOf(this),
                    currentIndex,
                    -1
                )
            }

            PEEP_SUCCESS -> {
                if (removedBuffer != null) {
                    removedBuffer[0] = stack.pop(currentIndex)
                } else {
                    stack.pop(currentIndex)
                }
            }

            PEEP_SUCCESS_2SLOT -> {
                if (removedBuffer != null) {
                    removedBuffer[0] = stack.pop(currentIndex)
                    removedBuffer[1] = stack.pop(currentIndex)
                } else {
                    stack.pop(currentIndex)
                    stack.pop(currentIndex)
                }
            }

            PEEP_FAIL_2SLOT -> {
                produceError(
                    byteCode,
                    stack.toTypedArray(),
                    arrayOf(this),
                    currentIndex,
                    -2
                )
            }
        }
    }

    private fun Array<SWord>.popFromListReverse(
        stack: ArrayDeque<SWordDebug>,
        byteCode: KBByteCode,
        additional: SWord?,
        listToReturn: Array<SWordDebug>?,
        currentIndex: Int,
    ) {
        val end = if (additional != null) -1 else 0
        var arIdx = 0
        val stackSnapshot = stack.toTypedArray() // For purposes of debugging
        for (i in size - 1 downTo end) { // reverse
            val w = if (i == -1) {
                additional!!
            } else this[i]
            when (stack.peepCompare(w)) {
                PEEP_FAIL_2SLOT, PEEP_FAIL -> {
                    val sn = buildList<SWord?> {
                        if (additional != null) {
                            add(additional)
                        }
                        addAll(this@popFromListReverse)

                    }.toTypedArray()
                    produceError(
                        byteCode,
                        stackSnapshot as Array<SWordDebug?>,
                        sn,
                        currentIndex,
                        (stackSnapshot.size - 1) - (size - 1) + i
                    )
                }

                PEEP_SUCCESS -> {
                    if (listToReturn == null) {
                        stack.pop(currentIndex)
                    } else {
                        listToReturn[arIdx] = stack.pop(currentIndex)//stack[stack.lastIndex])
                        ++arIdx
                    }
                }

                PEEP_SUCCESS_2SLOT -> {
                    if (listToReturn == null) {
                        stack.pop(currentIndex)
                        stack.pop(currentIndex)
                    } else {
                        listToReturn[arIdx] = stack.pop(currentIndex)//stack[stack.lastIndex])
                        ++arIdx
                        listToReturn[arIdx] = stack.pop(currentIndex)//stack[stack.lastIndex - 1])
                        ++arIdx
                    }
                }

//                2 -> {
//                    val newIndex = if (additional != null) i + 1 else i
//                    produceError(
//                        byteCode,
//                        "Stack needed: ${
//                            this.toMutableList().apply { if (additional != null) add(0, additional) }
//                        }, but instead of $w at index $newIndex a ${stack.removeLastOrNull()}:${stack.removeLastOrNull()} was received.",
//                        currentIndex
//                    )
//                }
            }
        }
    }

    private fun TypeInfo.getSWord(): SWord {
        return if (this is TypeInfo.JClassAvailable) {
            when (jClass) {
                Double::class.java -> (D)
                Long::class.java -> (L)
                Int::class.java -> (I)
                Short::class.java -> I.S
                Boolean::class.java -> I.Z
                Byte::class.java -> I.B
                Char::class.java -> I.C
                Float::class.java -> F
                else -> A(jClass)
            }
        } else {
            A.createFromTypeInfo(this)
        }
    }

    private fun produceError(
        byteCode: KBByteCode,
        actualStack: Array<SWordDebug?>,
        neededStack: Array<SWord?>,
        idxRT: Int,
        wrongIdx: Int,
        stackDiff: Boolean = false,
    ): Nothing {
        throw VerifierStackException(
            byteCode, actualStack, neededStack, idxRT, this.allByteCode, wrongIdx,
            IllegalStateException("Failed verification"), stackDiff, functionName
        )
    }

//    private val labelStackMap = hashMapOf<Label, List<SWord>>()

    private fun KBByteCode.unpack(): KBByteCode = if (this is KBByteCode.Debug) {
        this.value
    } else {
        this
    }

    private fun jumpToLabelInBuffer(l: Label, lp: LabelPresent, frame: Frame): Int {
        val idx = allByteCode.indexOfFirst { u ->
            val it = u.actual()//.unpack()
            it is LabelPoint && it.label == l
        }
        if (idx == -1) error("Could not find the label of $l")
        return analyze(frame, idx, lp)
    }

    // in before
    // first index is the top of stack
    // last index is whatever will be popped first


    private val labelMap = IdentityHashMap<Label, Frame>()

    private fun SWord.toDebug(bc: KBByteCode): SWordDebug {
        return SWordDebug(this, bc)
    }

    private fun analyze(
        frameSupplied: Frame,
        idx: Int,
        redirectionOrigin: LabelPresent?,
    ): Int {
        var jumpAlreadyChecked = redirectionOrigin == null
        if (redirectionOrigin != null) {
            require(allByteCode[idx].actual() is LabelPoint)
        }

        val stack = ArrayDeque<SWordDebug>(cSSize)
        val variables = IntObjectHashMap<SWordDebug>()
        variables.putAll(frameSupplied.variables)
        frameSupplied.before.forEach(stack::addLast)
        for (i in idx until allByteCode.size) {
            val byteCodeOld = allByteCode[i]
            val byteCode = byteCodeOld.actual()
            when (byteCode) {
                is KBAsmOp -> {
                    byteCode.stackTakes?.forEach {
                        it.popCompareSafely(stack, byteCodeOld, currentIndex = i)
                    }
                    byteCode.stackGives?.forEach {
                        stack.push(it, byteCodeOld)
                    }
                }

                is KBFieldOP -> {
                    when (byteCode.instruction) {
                        PUTSTATIC -> {
                            byteCode.descriptor.getSWord().popCompareSafely(stack, byteCodeOld, currentIndex = i)
                        }

                        GETSTATIC -> {
                            stack.push(byteCode.descriptor.getSWord(), byteCodeOld)
                        }

                        PUTFIELD -> {
                            byteCode.descriptor.getSWord().popCompareSafely(stack, byteCodeOld, currentIndex = i)
                            byteCode.owner.getSWord().popCompareSafely(stack, byteCodeOld, currentIndex = i)
                        }

                        GETFIELD -> {
                            byteCode.owner.getSWord().popCompareSafely(stack, byteCodeOld, currentIndex = i)
                            stack.push(byteCode.descriptor.getSWord(), byteCodeOld)
                        }
                    }
                }

                is KBIincOP -> Unit
                is KBLdcOP -> {
                    when (byteCode.value) {
                        is Double -> stack.push(D, byteCodeOld)
                        is Long -> stack.push(L, byteCodeOld)
                        is Float -> stack.push(F, byteCodeOld)
                        is Byte -> stack.push(I.B, byteCodeOld)
                        is Short -> stack.push(I.S, byteCodeOld)
                        is Char -> stack.push(I.C, byteCodeOld)
                        is Boolean -> stack.push(I.Z, byteCodeOld)

                        is Int -> stack.push(I, byteCodeOld) // I = plain 32-bit int
                        else -> if (byteCode.type == LdcType.MethodType) {
                            val newValue = (byteCode.value as TypeInfo)

                            stack.push(A.createFromNullableClass(newValue.asClassOfClass().jClass), byteCodeOld)
                        } else {
                            if (byteCode.type != LdcType.String) error("Not yet supported")
                            stack.push(A(String::class.java), byteCodeOld)
                        }
                    }
                }

                is KBMethodCallOP -> {// && !byteCode.descriptor.firstIsCallTo(byteCode.owner)
                    val additional = if (byteCode.instruction.opcode != MethodInsnOp.INVOKESTATIC.opcode) {
                        byteCode.owner.getSWord()
                    } else null
                    byteCode.descriptor.getArgsSWordArray()
                        .popFromListReverse(stack, byteCodeOld, additional, null, currentIndex = i)
                    byteCode.descriptor.getReturnSWord()?.let {
                        stack.push(it, byteCodeOld)
                    }
                }

                is KBSingleIntOP -> {
                    // no stack taken
                    if (byteCode.instruction.opcode == 188) {
                        IntInsnOp.NEWARRAY.stackTaken!!.popCompareSafely(stack, byteCodeOld, null, i)
                    }
                    stack.push(byteCode.instruction.stackGiven.ifAAddClassInfo(byteCode.typeInfo), byteCodeOld)
                }

                is KBSingleOP -> {
                    if (determineKBSingleOP(byteCode, byteCodeOld, stack, currentIndex = i)) {
                        break
                    }
                }

                is KBTypeOP -> {
                    byteCode.instruction.stackTaken?.popCompareSafely(stack, byteCodeOld, currentIndex = i)
                    when (byteCode.instruction.opcode) {
                        193 -> { // instance of - just pushes int
                            stack.push(TypeInsnOp.INSTANCEOF.stackGiven, byteCodeOld)
                        }
                        189 -> {
                            stack.push(byteCode.instruction.stackGiven.ifAAddClassInfo(KArray(byteCode.typeInfo)), byteCodeOld)
                        }
                        else -> {
                            stack.push(byteCode.instruction.stackGiven.ifAAddClassInfo(byteCode.typeInfo), byteCodeOld)
                        }
                    }
                }

                is KBVariableOP -> {
                    if (byteCode.instruction.isLoadKind) {
                        when (val checkLoad = VariableProblem.checkLoad(byteCode, variables)) {
                            VariableProblem.CHECK_LOAD_FINE -> {
                                val sWordDebug = variables[byteCode.variableIndex]!!
                                stack.push(sWordDebug.kind, byteCodeOld)
//                                byteCode.instruction.stackGiven?.let { stack.push(it, byteCodeOld) }
                            }
                            VariableProblem.CHECK_LOAD_NOT_FOUND -> {
                                throw VerifierVariableUninitializedException(
                                    byteCodeOld,
                                    allByteCode,
                                    java.lang.IllegalStateException(),
                                    functionName
                                )
                            }

                            VariableProblem.CHECK_LOAD_WRONG_KIND -> {
                                throw VerifierVariableWrongKindException(
                                    byteCodeOld,
                                    variables[byteCode.variableIndex]!!.origin,
                                    allByteCode,
                                    java.lang.IllegalStateException(),
                                    false,
                                    functionName
                                )
                            }

                            else -> error("checkLoad = $checkLoad. Which is an unexpected return type!")
                        }
                    } else if (byteCode.instruction.isStoreKind) {
                        val checkStore = VariableProblem.checkStore(byteCode, variables)
                        if (checkStore != VariableProblem.CHECK_STORE_FINE) {
                            when (checkStore) {
                                VariableProblem.CHECK_STORE_OCCUPIES_W64 -> throw VerifierVariableWrongKindException(
                                    byteCodeOld,
                                    variables[byteCode.variableIndex - 1]!!.origin,
                                    allByteCode,
                                    java.lang.IllegalStateException(),
                                    true,
                                    functionName
                                )
                                else -> error("checkStore = $checkStore. Which is an unexpected return type!")
                            }
                        } else {
                            val removedBuffer = arrayOfNulls<SWordDebug>(1) as Array<SWordDebug>
                            byteCode.instruction.stackTaken!!.popCompareSafely(stack, byteCodeOld, removedBuffer, currentIndex = i)

                            val kind = removedBuffer[0].kind
                            if (byteCode.descriptorWasNull) {
                                byteCode.descriptor = kind.toTypeInfo() ?: TypeInfo.Object
                            }

                            variables.put(
                                byteCode.variableIndex,
                                SWordDebug(kind, byteCodeOld) // we add to preserve type info if there was any
                            )
                        }
                    }
                }

                is LabelPoint -> {
                    val frameInMap = labelMap[byteCode.label]
                    if (frameInMap != null) {
                        val nf = Frame(stack.toList(), variables)
                        if (!frameInMap.isAcceptable(nf)) {
                            produceError(
                                byteCodeOld,
                                nf.before.toTypedArray(),
                                frameInMap.before.mapA { it.kind },
                                i,
                                i,
                                true
                            )
                        }
                        if (!jumpAlreadyChecked) { // after any jump label point must be the first to be seen
                            if (frameInMap.wasHereBefore(redirectionOrigin!!)) {
                                return -2
                            }
                            frameInMap.story.putAll(frameSupplied.story)
                            jumpAlreadyChecked = true
                        }
//                        frameInMap.story.putAll(nf.story)
                    } else {
                        labelMap[byteCode.label] = Frame(stack.toList(), variables)
                    }
                }

                is LabelPresent -> {
                    if (determineLabel(byteCode, stack, variables, byteCodeOld, i) == -1) {
                        break
                    }
                }

                is KBByteCode.Debug -> error("Should not be here")
                is DebugStack -> {
                    byteCode.debug(stack.map { it.kind })
                }

                is JustPrint -> Unit // Ignore
                is ByteCodeMetaData -> Unit
                is KBInvokeDyn -> {
                    byteCode.descriptor.getArgsSWordArray()
                        .popFromListReverse(stack, byteCodeOld, null, null, currentIndex = i)
                    byteCode.descriptor.getReturnSWord()?.let {
                        stack.push(it, byteCodeOld)
                    }
                }
            }
        }
        return -2
    }

    private fun markLabel(lp: LabelPresent, frame: Frame, label: Label): Boolean {
        val frameInMap = labelMap[label]
        if (frameInMap != null && frameInMap.story.containsKey(lp)) {
            return true
        }
        frame.story.put(lp, null)
        return false
    }

    private fun SWord.ifAAddClassInfo(classInfo: TypeInfo?): SWord {
        if (classInfo == null) return this
        return if (this === A) {
            A.createFromTypeInfo(classInfo)
        } else {
            this
        }
    }

    @Suppress("DuplicatedCode")
    private fun determineLabel(
        labelPresent: LabelPresent,
        stack: ArrayDeque<SWordDebug>,
        variables: IntObjectHashMap<SWordDebug>,
        byteCodeOld: KBByteCode,
        currentIndex: Int,
    ): Int {
        return when (labelPresent) {
            is KBJumpOP -> {
                labelPresent.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    null,
                    currentIndex = currentIndex
                )
                val frame = Frame(stack.toList(), variables)

                // Checks if this path was already checked before so not to fall into infinity
                if (markLabel(labelPresent, frame, labelPresent.label)) {
                    return -1
                }

                jumpToLabelInBuffer(labelPresent.label, labelPresent, frame)
                if (labelPresent.instruction === JumpInsnOp.GOTO) {
                    -1
                } else {
                    0
                }
            }

            is KBLookupSwitchOP -> {
                I.popCompareSafely(stack, byteCodeOld, null, currentIndex)
                val frame = Frame(stack.toList(), variables)
                if (markLabel(
                        labelPresent,
                        frame,
                        labelPresent.default
                    )
                ) { // I mean we do not need to check all cases anyway
                    return -1
                }
                jumpToLabelInBuffer(labelPresent.default, labelPresent, frame)
                labelPresent.cases.forEach {
                    jumpToLabelInBuffer(it, labelPresent, frame)
                }
                -1
            }

            is KBTableSwitchOP -> {
                I.popCompareSafely(stack, byteCodeOld, null, currentIndex)
                val frame = Frame(stack.toList(), variables)
                if (markLabel(
                        labelPresent,
                        frame,
                        labelPresent.default
                    )
                ) { // I mean we do not need to check all cases anyway
                    return -1
                }
                jumpToLabelInBuffer(labelPresent.default, labelPresent, frame)
                labelPresent.cases.forEach {
                    jumpToLabelInBuffer(it, labelPresent, frame)
                }
                -1
            }

            is KBTryCatchBlockOP -> {
                // it is assumed that user will add jump instruction after try block to catch end block
                val throwableType = labelPresent.exceptionType.recoverJClass()

                val frame = Frame(listOf(SWordDebug(A(throwableType), JustPrint("THROW"))), variables) // since catch block removes all the stack. We only append the throwable
                if (markLabel(
                        labelPresent,
                        frame,
                        labelPresent.startCatch
                    )
                ) {
                    return -1
                }
                // we need to check this path without any stack btw.
                jumpToLabelInBuffer(labelPresent.startCatch, labelPresent, frame)
                return 0
            }
        }
    }

    @Suppress("SENSELESS_COMPARISON", "UNCHECKED_CAST")
    private fun determineKBSingleOP(
        byteCode: KBSingleOP,
        byteCodeOld: KBByteCode,
        stack: ArrayDeque<SWordDebug>,
        currentIndex: Int,
    ): Boolean {
        val opcode = byteCode.instruction.opcode
        when (opcode) {
            95 -> { // InsnOp.SWAP
                val buffer = arrayOfNulls<SWordDebug>(2) as Array<SWordDebug>
                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    buffer,
                    currentIndex = currentIndex
                )
//                println(buffer.toList())
//                for (word in buffer) {
//                    stack.push(word)
//                }
                stack.push(buffer[0])
                stack.push(buffer[1])
            }

            89 -> { // InsnOp.DUP.opcode
                val stackRemoved = arrayOfNulls<SWordDebug>(1) as Array<SWordDebug>
                byteCode.instruction.stackTaken[0].popCompareSafely(
                    stack,
                    byteCodeOld,
                    stackRemoved,
                    currentIndex = currentIndex
                )
                val toPush = stackRemoved[0]
                stack.push(toPush)
                stack.push(toPush)
            }

            92 -> { // InsnOp.DUP2.opcode
                val stackRemoved = arrayOfNulls<SWordDebug>(2) as Array<SWordDebug>
//                println(stack.toList())
                byteCode.instruction.stackTaken[0].popCompareSafely(
                    stack,
                    byteCodeOld,
                    stackRemoved,
                    currentIndex = currentIndex
                )
//                println(stackRemoved.toList())
                if (stackRemoved[1] == null) {
                    val toPush = stackRemoved[0]
                    stack.push(toPush)
                    stack.push(toPush)
                } else {
                    stack.push(stackRemoved[1])
                    stack.push(stackRemoved[0])
                    stack.push(stackRemoved[1])
                    stack.push(stackRemoved[0])
                }
//                println("New stack ${stack.toList()}")
            }

            90 -> { // InsnOp.DUP_X1.opcode
                // 1
                // 2
                // 3
                // DUP_X1
                //
                val stackRemoved = arrayOfNulls<SWordDebug>(2) as Array<SWordDebug>
                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    stackRemoved,
                    currentIndex = currentIndex
                )
                stack.push(stackRemoved[0])
                stack.push(stackRemoved[1])
                stack.push(stackRemoved[0])
            }

            91 -> { // DUP_X2
                val stackRemoved = arrayOfNulls<SWordDebug>(3) as Array<SWordDebug> // can be two as well
                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    stackRemoved,
                    currentIndex = currentIndex
                )
                stack.push(stackRemoved[0])
                @Suppress("UNNECESSARY_SAFE_CALL")
                stackRemoved[2]?.let {
                    stack.push(it)
                }
                stack.push(stackRemoved[1])
                stack.push(stackRemoved[0])
            }

            93 -> { //DUP2_X1
                val stackRemoved = arrayOfNulls<SWordDebug>(3) as Array<SWordDebug> // can be two as well
                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    stackRemoved,
                    currentIndex = currentIndex
                )
                if (stackRemoved[2] != null) {
                    stack.push(stackRemoved[1])
                    stack.push(stackRemoved[0])
                    stack.push(stackRemoved[2])
                    stack.push(stackRemoved[1])
                    stack.push(stackRemoved[0])
                } else {
                    stack.push(stackRemoved[0])
                    stack.push(stackRemoved[1])
                    stack.push(stackRemoved[0])
                }
            }

            94 -> { //DUP2_X2
                val stackRemoved = arrayOfNulls<SWordDebug>(4) as Array<SWordDebug> // can be two as well
                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    stackRemoved,
                    currentIndex = currentIndex
                )
                val first = stackRemoved[0]
                val second = stackRemoved[1]
                if (first.kind is W64) {
                    stack.push(first)
                    if (second.kind is W64) {
                        stack.push(second)
                        stack.push(first)
                    } else { // second is two slot
                        stack.push(stackRemoved[2])
                        stack.push(second)
                        stack.push(first)
                    }
                } else {
                    stack.push(second)
                    stack.push(first)
                    val third = stackRemoved[2]
                    if (third.kind is W64) {
                        stack.push(third)
                        stack.push(second)
                        stack.push(first)
                    } else { // third is two slot
                        stack.push(stackRemoved[3])
                        stack.push(third)
                        stack.push(second)
                        stack.push(first)
                    }
                }
            }

            else -> {
                //IALOAD      = InsnOp(46
                //LALOAD      = InsnOp(47
                //FALOAD      = InsnOp(48
                //DALOAD      = InsnOp(49
                //AALOAD      = InsnOp(50
                //BALOAD      = InsnOp(51
                //CALOAD      = InsnOp(52
                //SALOAD      = InsnOp(53
                if (opcode == 50) {
                    val removedBuffer = arrayOfNulls<SWordDebug>(2) as Array<SWordDebug>
                    byteCode.instruction.stackTaken.popFromListReverse(
                        stack,
                        byteCodeOld,
                        null,
                        removedBuffer,
                        currentIndex = currentIndex
                    )

                    val idOfArr = removedBuffer[0]
                    val arrRef = removedBuffer[1]

                    val elType = figureOutActualArrayLoad(arrRef)
                    stack.push(A(elType), byteCodeOld)

                    return false
                }



                byteCode.instruction.stackTaken.popFromListReverse(
                    stack,
                    byteCodeOld,
                    null,
                    null,
                    currentIndex = currentIndex
                )
                byteCode.instruction.stackGiven.forEach {
                    stack.push(it, byteCodeOld)
                }
                InsnOp.ARETURN
                //IRETURN     = InsnOp(172, arrayOf(I),
                //LRETURN     = InsnOp(173, arrayOf(L),
                //FRETURN     = InsnOp(174, arrayOf(F),
                //DRETURN     = InsnOp(175, arrayOf(D),
                //ARETURN     = InsnOp(176, arrayOf(A),
                //RETURN      = InsnOp(177, arrayOf(),
                when (opcode) {
                    172, 173, 174, 175, 176 -> {
                        if (byteCode.instruction.stackTaken[0] != returnType.toSWord()) {
                            error("Return type is different!")
                        }
                        return true
                    } // if it is a return
                    177 -> return true
                    191 -> return true // if it is a throw
                }
            }
        }
        return false
    }

    fun Class<*>.toSWord(): SWord {
        return when (this) {
            Int::class.javaPrimitiveType,
            Short::class.javaPrimitiveType,
            Char::class.javaPrimitiveType, Boolean::class.javaPrimitiveType,
                -> I

            Double::class.javaPrimitiveType -> D
            Float::class.javaPrimitiveType -> F
            Long::class.javaPrimitiveType -> L
            else -> A
        }
    }


    fun run() {
        // Also update variable
        val variables = IntObjectHashMap<SWordDebug>()

        if (!isStatic) {
            variables.put(0, SWordDebug(A.createFromTypeInfo(thisClassInfo), JustPrint("ThisClass", "this A 0")))
        }

        parameters?.forEach {
            variables.put(it.index, SWordDebug(it.typeInfo.getSWord(), JustPrint(it.name, "param ${it.typeInfo.getSWord()} ${it.index}")))
        }

        analyze(Frame(emptyList(), variables), 0, null)
    }
}


