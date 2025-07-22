package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.lan.compiler.VariableManager

abstract class StackInfo {

    // Returns min value of int if it does not want variable assignment
    abstract val assignIndex: Int

    val variableAssignmentMode: Boolean get() = assignIndex != Int.MIN_VALUE

    fun conditionLabel(): Label? {
        return null
    }

    // Will be null if function does not want assignable stuff
    fun assignIndexOrNull(): Int? {
        return null // currently always
        val i = assignIndex
        return if (i == Int.MIN_VALUE) {
            null
        } else {
            i
        }
    }

    // Just produces a new variable index
    // specify cat1 or cat2 in double
    abstract fun getVariableIndex(double: Boolean, name: String? = null): Int

    fun getVariableIndexAuto(clazz: Class<*>, name: String? = null): Int {
        val double = when (clazz) {
            Double::class.java, Long::class.java -> true
            else -> false
        }

        return getVariableIndex(double, name)
    }


    // argument stuff
    @Deprecated("use different version", replaceWith = ReplaceWith("pushArgument(i)"))
    fun pushArgument(kbMethod: KBMethod, i: Int) {
        pushArgument(i)
    }


    abstract fun pushArgument(i: Int)
//    @JvmName("pushArgumentNR")
//    fun pushArgument(kbMethod: KBMethod, i: Int) {
//        kbMethod.pushArgument(i)
//    }
}

class StackInfoImpl(
    private val indexToAssign: Int,
    val variableManager: VariableManager, //var availableIndex: Int,
    private val argArray: Array<KBMethod.() -> Unit>,
    private val autoPushEnabled: Boolean,
    private val kbMethod: KBMethod
) : StackInfo() {


    override val assignIndex: Int = Int.MIN_VALUE

    override fun getVariableIndex(double: Boolean, name: String?): Int {
        return variableManager.variableIndex(name ?: variableManager.createAnonName(), double)
    }

    override fun pushArgument(i: Int) {
        if (autoPushEnabled) { // todo there is something horribly wrong with this
            error("Auto push is enabled")
        }
        argArray[i].invoke(kbMethod)
    }
}