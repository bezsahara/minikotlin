package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.method.KBLookupSwitchOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBTableSwitchOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label

fun KBMethod.tableSwitch(
    min: Int,
    max: Int,
    default: Label,
    cases: Array<Label>
) {
    addOperation(KBTableSwitchOP(
        min,
        max,
        default,
        cases
    ))
}

fun KBMethod.tableSwitch(
    range: IntRange,
    default: Label,
    cases: Array<Label>
) {
    tableSwitch(range.first, range.last, default, cases)
}

fun KBMethod.lookupSwitch(
    default: Label,
    keys: IntArray,
    cases: Array<Label>
) {
    addOperation(KBLookupSwitchOP(
        default,
        keys,
        cases
    ))
}


fun KBMethod.lookupSwitch(
    default: Label,
    cases: Array<Pair<Int, Label>>
) {
    val size = cases.size
    addOperation(KBLookupSwitchOP(
        default,
        IntArray(size) {
            cases[it].first
        },
        Array(size) {
            cases[it].second
        }
    ))
}
