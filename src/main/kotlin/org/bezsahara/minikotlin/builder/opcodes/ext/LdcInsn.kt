package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.method.KBLdcOP

fun KBMethod.ldc(value: String) {
    addOperation(KBLdcOP(value, ldcCounter))
}

fun KBMethod.ldc(typeInfo: TypeInfo) {
    addOperation(KBLdcOP(typeInfo, ldcCounter))
}

fun KBMethod.ldc(number: Number) {
    addOperation(KBLdcOP(number, ldcCounter))
}

fun KBMethod.ldc(number: Char) {
    addOperation(KBLdcOP(number.code, ldcCounter))
}

fun KBMethod.ldc(boolean: Boolean) {
    if (boolean) {
        addOperation(KBLdcOP(1, ldcCounter))
    } else {
        addOperation(KBLdcOP(0, ldcCounter))
    }
}


fun KBMethod.ldcOptimized(number: Number) {
    when (number) {
        is Long -> when (number) {
            0L -> lconst_0()
            1L -> lconst_1()
            else -> ldc(number)
        }
        is Double -> when (number) {
            0.0 -> dconst_0()
            1.0 -> dconst_1()
            else -> ldc(number)
        }
        is Float -> when (number) {
            0f -> fconst_0()
            1f -> fconst_1()
            2f -> fconst_2()
            else -> ldc(number)
        }
        else -> when (number.toInt()) {
            -1 -> iconst_m1()
            0 -> iconst_0()
            1 -> iconst_1()
            2 -> iconst_2()
            3 -> iconst_3()
            4 -> iconst_4()
            5 -> iconst_5()
            else -> {
                when (number) {
                    in Byte.MIN_VALUE..Byte.MAX_VALUE -> bipush(number.toByte())
                    in Short.MIN_VALUE..Short.MAX_VALUE -> sipush(number.toShort())
                    else -> ldc(number)
                }
            }
        }
    }
}

fun KBMethod.ldcOptimized(boolean: Boolean) = if (boolean) {
    iconst_1()
} else iconst_0()

fun KBMethod.ldcOptimized(number: Int) {
    when (number) {
        -1 -> iconst_m1()
        0 -> iconst_0()
        1 -> iconst_1()
        2 -> iconst_2()
        3 -> iconst_3()
        4 -> iconst_4()
        5 -> iconst_5()
        else -> {
            when (number) {
                in Byte.MIN_VALUE..Byte.MAX_VALUE -> bipush(number.toByte())
                in Short.MIN_VALUE..Short.MAX_VALUE -> sipush(number.toShort())
                else -> ldc(number)
            }
        }
    }
}