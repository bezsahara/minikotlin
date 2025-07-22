package org.bezsahara.minikotlin.lan.lib

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.MDString
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.opcodes.codes.ACommon
import org.bezsahara.minikotlin.builder.opcodes.codes.SWord
import org.bezsahara.minikotlin.builder.opcodes.ext.invokevirtual
import org.bezsahara.minikotlin.builder.opcodes.ext.newAndInit
import org.bezsahara.minikotlin.lan.KRef
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.MiniKotlin
import org.bezsahara.minikotlin.lan.StackInfo

fun stBuild() : String {
    val sb = StringBuilder()
    sb.append("point1")
    sb.append("point2")
    sb.append(2)
    return sb.toString()
}
//INVOKEVIRTUAL java/lang/StringBuilder.append (Ljava/lang/String;)Ljava/lang/StringBuilder
class MiniStringBuilder(
    val refs: Array<KRef<*>>
) : KValue.ValueBlockReturns(refs) {
    override val autoPush: Boolean = false
    private val owner = TypeInfo.Java(StringBuilder::class.java)

    override fun KBMethod.returns(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        newAndInit(TypeInfo.Java(StringBuilder::class.java))

        repeat(refs.size) {
            stackInfo.pushArgument(it)
            invokevirtual(owner, "append", descriptorAppend)
        }

        invokevirtual(owner, "toString", MDString("()Ljava/lang/String;", emptyArray(), ACommon.Str))
    }

    override val objType: TypeInfo = TypeInfo.String
    companion object {
        private val descriptorAppend =
            MDString("(Ljava/lang/String;)Ljava/lang/StringBuilder;", arrayOf(ACommon.Str), SWord.A(StringBuilder::class.java))
    }
}

fun joinStringsOf(vararg refs: KRef.Obj<String>): KRef.Obj<String> {
    return KRef.Obj(String::class, MiniStringBuilder(refs as Array<KRef<*>>))
}

fun MiniKotlin<*>.echoLine(vararg refs: KRef.Obj<String>) {
    echoLine(joinStringsOf(*refs))
}