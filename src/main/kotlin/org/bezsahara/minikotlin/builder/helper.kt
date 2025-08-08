package org.bezsahara.minikotlin.builder

fun repr(value: String?): String = when (value) {
    null -> "null"
    else -> buildString {
        append('"')
        for (c in value) {
            when (c) {
                '\\' -> append("\\\\")
                '"'  -> append("\\\"")
                '\n' -> append("\\n")
                '\r' -> append("\\r")
                '\t' -> append("\\t")
                else -> {
                    val code = c.code
                    if (code < 0x20 || code > 0x7E) {
                        append("\\u").append(code.toString(16).padStart(4, '0'))
                    } else {
                        append(c)
                    }
                }
            }
        }
        append('"')
    }
}