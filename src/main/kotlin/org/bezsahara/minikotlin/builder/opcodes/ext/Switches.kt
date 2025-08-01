package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.method.KBLookupSwitchOP
import org.bezsahara.minikotlin.builder.opcodes.method.KBTableSwitchOP
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import java.util.Arrays

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


fun KBMethod.autoSwitch(
    default: Label,
    keys: IntArray,
    cases: Array<Label>
) {
    Arrays.sort(keys)

    val min = keys.first()
    val max = keys.last()

    val range = max - min + 1
    val density = keys.size.toDouble() / range

    // JVM tends to prefer tableswitch if at least ~50% of range is covered

    if (density >= 0.5) {
        tableSwitch(min, max, default, Array(range) { i ->
            val idx = keys.indexOf(min + i)
            if (idx == -1) default else cases[idx]
        })
    } else {
        lookupSwitch(default, keys, cases)
    }
}