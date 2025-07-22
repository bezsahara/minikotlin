package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.returns
import org.bezsahara.minikotlin.builder.declaration.typeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.helpers.printStreamType
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.StackInfo
import org.bezsahara.minikotlin.lan.pieces.CanExplainNull
import org.bezsahara.minikotlin.lan.pieces.CustomActionPiece
import java.io.PrintStream


fun str(s: String): KRef.Obj<String> {
    return KRef.Obj(String::class, object : KValue.ValueBlockReturns() {
        override val objType: TypeInfo = TypeInfo.String

        override fun KBMethod.returns(variables: Map<String, Int>, stackInfo: StackInfo) {
            ldc(s)
        }
    })
}

fun MiniKotlin<*>.echo(k: KRef.Obj<String>) {
    performAction(object : KValue.ValueBlock(arrayOf(k)) {
        override val autoPush: Boolean
            get() = false
        override fun KBMethod.init(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            getstatic(TypeInfo.Kt(System::class), "out", typeInfo<PrintStream>())
            stackInfo.pushArgument(0)
            invokevirtual(printStreamType, "print", args(String::class))
        }
    })
}

fun MiniKotlin<*>.echoLine(k: KRef.Obj<String>) {
    addPiece(CustomActionPiece(object : KValue.ValueBlock(arrayOf(k)) {
        override val autoPush: Boolean
            get() = false
        override fun KBMethod.init(
            variables: Map<String, Int>,
            stackInfo: StackInfo,
        ) {
            getstatic(TypeInfo.Kt(System::class), "out", printStreamType)
            stackInfo.pushArgument(0)
            invokevirtual(printStreamType, "println", args(String::class))
        }
    }))
}

class ToStringConversion(val convertee: KRef<*>, val safeMode: Boolean) : KValue.ValueBlockReturns(convertee) {
    override val objType: TypeInfo = TypeInfo.String

    private fun KBMethod.insert() {
        val nullLabel = Label()
        val final = Label()
        dup()
        ifnull(nullLabel)
//                invokestatic(TypeInfo.Object, "valueOf", args(convertee.kClass) returns String::class)
        invokevirtual(TypeInfo.Object, "toString", returns(String::class))//INVOKEVIRTUAL java/lang/Object.toString ()Ljava/lang/String;
        goto(final)

        labelPoint(nullLabel)
        pop() // TODO make sure POP here is working okay in verifier
        ldc("null")

        labelPoint(final)
    }

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {

        when (convertee) {
            is KRef.Native<*> -> {
                if (convertee.jClass === Short::class.java) {
                    invokestatic(TypeInfo.Java(String::class.java), "valueOf", args(TypeInfo.Int) returns String::class)
                } else {
                    invokestatic(TypeInfo.Java(String::class.java), "valueOf", args(TypeInfo.Java(convertee.jClass)) returns String::class)
                }
            }
            is KRef.Obj<*> -> {
                val value = convertee.value
                if (value is CanExplainNull) {
                    if (value.isNullable()) {
                        insert()
                    } else {
                        invokevirtual(TypeInfo.Object, "toString", returns(String::class))//INVOKEVIRTUAL java/lang/Object.toString ()Ljava/lang/String;
                    }
                } else if (safeMode) {
                    insert()
                } else {
                    invokevirtual(TypeInfo.Object, "toString", returns(String::class))//INVOKEVIRTUAL java/lang/Object.toString ()Ljava/lang/String;
                }
            }
            else -> Unit
        }
    }
}

context(mk: MiniKotlin<*>)
fun KRef<*>.toStr(): KRef.Obj<String> {
    return KRef.Obj(String::class, ToStringConversion(this, true))
}

context(mk: MiniKotlin<*>)
fun KRef<*>.toStrUnsafe(): KRef.Obj<String> {
    return KRef.Obj(String::class, ToStringConversion(this, false))
}

fun tyrt(a: Any) {
    a.toString()
}