package org.bezsahara.minikotlin.builder.opcodes.helpers

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MethodDescriptor
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.opcodes.ext.getstatic
import org.bezsahara.minikotlin.builder.opcodes.ext.invokevirtual
import org.bezsahara.minikotlin.builder.opcodes.ext.ldc
import org.bezsahara.minikotlin.builder.opcodes.ext.swap
import java.io.PrintStream


val printStreamType = TypeInfo.Kt(PrintStream::class)
private val printLnDescriptor = MethodDescriptor(
    arrayOf(TypeInfo.Object),
    TypeInfo.Void
)

fun KBMethod.printI() {
    getstatic(TypeInfo.Kt(System::class), "out", printStreamType)
    swap()
    invokevirtual(printStreamType, "println", args(Int::class) returns TypeInfo.Void)
}

fun KBMethod.printObj() {
    getstatic(TypeInfo.Kt(System::class), "out", printStreamType)
    swap()
    invokevirtual(printStreamType, "println", printLnDescriptor)
}

fun KBMethod.print(string: String) {
    getstatic(TypeInfo.Kt(System::class), "out", printStreamType)
    ldc(string)
    invokevirtual(printStreamType, "println", printLnDescriptor)
}