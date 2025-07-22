package org.bezsahara.minikotlin.lan.pieces

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.sameAs
import org.bezsahara.minikotlin.builder.opcodes.ext.pop
import org.bezsahara.minikotlin.builder.opcodes.ext.pop2
import org.bezsahara.minikotlin.lan.*

interface NoCastNeeded

interface CanExplainNull {
    fun isNullable(): Boolean
}

fun KValue.isNullable(): Boolean {
    return this is CanExplainNull && isNullable()
}

class NoStackAppendAction(private val ref: KRef<*>) : KValue.ValueBlock(arrayOf(ref)) {
    override val autoPush: Boolean = true


    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        when (ref) {
            is KRef.Native<*> -> {
                when (ref.jClass) {
                    dt, lt -> pop2()
                    else -> pop()
                }
            }
            is KRef.Obj<*> -> { //(ref.kClass as ClassBasedDeclarationContainer).jClass
                if (!(ref.value.getType() sameAs vt)) {
                    pop()
                }
            }
            else -> error("Not supported ref: $ref")
        }
    }
    
    companion object {
        private val vt = TypeInfo.Void
        private val dt = java.lang.Double.TYPE
        private val lt = java.lang.Long.TYPE
    }
}

context(mk: MiniKotlin<*>)
fun KRef<*>.exec() {
    mk.performAction(NoStackAppendAction(this))
}

@JvmName("exec2")
fun MiniKotlin<*>.exec(r: KRef<*>) {
    performAction(NoStackAppendAction(r))
}