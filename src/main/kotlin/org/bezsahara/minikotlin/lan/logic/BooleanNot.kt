package org.bezsahara.minikotlin.lan.logic

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.goto
import org.bezsahara.minikotlin.builder.opcodes.ext.iconst_1
import org.bezsahara.minikotlin.builder.opcodes.ext.ifeq
import org.bezsahara.minikotlin.builder.opcodes.ext.ixor
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

class BooleanNot(ref: KRef<*>): KValue.ValueBlockReturns(ref), CanAcceptLabels {

    var goodLabel: Label? = null
    var badLabel: Label? = null

    override fun trySupplyLabels(
        successLabel: Label,
        failureLabel: Label,
    ): Boolean {
        goodLabel = successLabel
        badLabel = failureLabel
        return true
    }

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        if (goodLabel != null) {
            ifeq(goodLabel!!)
            goto(badLabel!!)
        } else {
            iconst_1()
            ixor()
        }
    }

    override val objType: TypeInfo = TypeInfo.Boolean

}

private fun anyNot(ref: KRef.Native<Boolean>): KRef.Native<Boolean> {
    val value = ref.value
    if (value is NegationIsPossible && value.isNotInlinePossible()) {
        return ref
    }

    return KRef.Native(Boolean::class, BooleanNot(ref))
}

fun KRef.Native<Boolean>.not(): KRef.Native<Boolean> {
    return anyNot(this)
}

@JvmName("notInner")
fun not(r: KRef.Native<Boolean>): KRef.Native<Boolean> {
    return anyNot(r)
}