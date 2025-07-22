package org.bezsahara.minikotlin.builder

//import org.w3c.dom.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.DP
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.occupiesTwo
import org.bezsahara.minikotlin.builder.opcodes.DebugInfo
import org.bezsahara.minikotlin.builder.opcodes.method.DebugStack
import org.bezsahara.minikotlin.builder.opcodes.method.KBByteCode
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.compiler.Optimizations
import org.bezsahara.minikotlin.compiler.asm.MetadataPass
import org.bezsahara.minikotlin.lan.logic.reduceLabelNoise

class KBMethod(
    val name: String,
    val methodProperty: DeclarationProperty<out DP.CanBeMethod, out TypeInfo>,
    val classProperties: ClassProperties
) {
    data class Result(
        val name: String,
        val methodProperty: DeclarationProperty<out DP.CanBeMethod, out TypeInfo>,
        val parameters: List<Parameter>,
        val operations: MutableList<KBByteCode>,
        val classProperties: ClassProperties
    )

    fun result(): Result {
        val methodParams = parameters.values.toList()
        var newOps = Optimizations.useAll(reduceLabelNoise(operations))
        if (classProperties.trackVariables) {
            newOps = MetadataPass().start(newOps, methodParams)
        }
        return Result(
            name,
            methodProperty,
            methodParams,
            newOps as ArrayList<KBByteCode>,
            classProperties
        )
    }

    val isStatic: Boolean get() = methodProperty.isStatic

    class Builder<T: Any>(
        val name: String,
        val methodProperty: DeclarationProperty<out DP.CanBeMethod, out TypeInfo>,
        val params: List<Parameter> = emptyList(),
        val classProperties: ClassProperties,
        val kbClass: KBClass
    ) {
        var body: (KBMethod.() -> Unit)? = null
//        var secondaryBody: KBMethod.(firstPresent: Boolean) -> Unit = {}
        private var cachedBuilt: KBMethod? = null

        infix fun runs(other: KBMethod.() -> Unit): ThisMethodAny {
            body = other
            val b = build()
            return ThisMethod(name, b.parameters.values.toList(), methodProperty)
        }

        fun build(): KBMethod {
            cachedBuilt?.let { return it }
            return KBMethod(
                name,
                methodProperty,
                classProperties
            ).also {
                params.forEach { kBMethodParameter ->
                    it.addParameter(kBMethodParameter)
                }
//                it.secondaryBody(body != null)
                body?.invoke(it)

                cachedBuilt = it
            }
        }
    }

    // Prints out the stack with a label you specify. For debugging purposes.
    // You can inspect stack here. It will only work if verifier is enabled, which is, unless you disabled it
    fun seeStack(label: String? = null) {
        addOperation(DebugStack(label))
    }

    // Same thing but you can impl it yourself
    // Also do not add new commands based on the stack, it will not work
    fun seeStack(debugStack: DebugStack) {
        addOperation(debugStack)
    }

    class Parameter(
        val name: String,
        val typeInfo: TypeInfo,
        val index: Int,
        val asDouble: Boolean
    )

    val parameters = linkedMapOf<String, Parameter>()
    val operations = java.util.ArrayList<KBByteCode>()

    private var parameterIndex = if (methodProperty.isStatic) 0 else 1

    private var parametersBlocked = false

    var lastVariableIndex = parameterIndex
        private set

    fun addParameter(name: String, typeInfo: TypeInfo): Int {
        val isDouble = typeInfo.occupiesTwo()
        val before = parameterIndex
        val param = Parameter(name, typeInfo, before, isDouble)
        return addParameter(param)
    }

    fun addParameter(parameter: Parameter): Int {
        if (parametersBlocked) {
            error("Cannot set parameters when body operations are specified")
        }
        parameters[parameter.name] = parameter
        parameterIndex += if (parameter.asDouble) 2 else 1
        return parameter.index
    }

    private val labelMap = hashMapOf<String, Label>()

    data class VariablePair(val varIndex: Int, val typeInfo: TypeInfo?, val name: String)
    private val variableMap = hashMapOf<String, VariablePair>()

    fun currentVarIndex(takeDouble: Boolean): Int {
        val pre = lastVariableIndex
        lastVariableIndex+=if (takeDouble) 2 else 1
        return pre
    }

    fun variable(name: String, typeInfo: TypeInfo?, double: Boolean): Int {
        val pTry = parameters[name]
        if (pTry != null) {
            return pTry.index
        }
        return variableMap.getOrPut(name) {
            VariablePair(currentVarIndex(double), typeInfo, name)
        }.varIndex
    }

    fun variable(name: String, isDouble: Boolean = false) = variable(name, null, isDouble)

//    inline fun <reified T: Any> typedVariable(name: String): VarIndex.Ref {
//        return VarIndex.Ref(variable(name, TypeInfo.Kt(T::class)))
//    }

//    inline fun <reified T: Number> nativeTypedVariable(name: String): VarIndex.Native<T> {
//        return VarIndex.Native<T>(variable(name, TypeInfo.Kt(T::class)))
//    }

    fun label(name: String): Label {
        var l = labelMap[name]
        if (l == null) {
            l = Label(name)
            labelMap[name] = l
        }
        return l
    }

    private val debugEnabled = classProperties.debug

    var ldcCounter = 1
        private set
        get() {
            return field++
        }

    fun addOperation(kbByteCode: KBByteCode) {
        val bc: KBByteCode = if (debugEnabled) {
            KBByteCode.Debug(kbByteCode, DebugInfo())
        } else kbByteCode
        if (!parametersBlocked) {
            parametersBlocked = true
        }
        (operations).add(bc)
    }

    inner class Capture(val startIndex: Int, val endIndex: Int) {
        val capturedOps = operations.subList(startIndex, endIndex+1)

        fun update(newOps: List<KBByteCode>) {
            capturedOps.clear()
            operations.addAll(startIndex, newOps)
        }

        fun reposition(newIndex: Int, newOps: List<KBByteCode>) {
            (capturedOps).clear()
            operations.addAll(newIndex, newOps)
        }

        fun reposition(newIndex: Int) {
            val newOps = (capturedOps.toTypedArray()).asList()
            capturedOps.clear()
            operations.addAll(newIndex, newOps)
        }
    }

    inline fun capture(block: () -> Unit): Capture {
        var startIndex = operations.lastIndex + 1
//        if (startIndex == -1) {
//            startIndex = 0
//        }
        block()
        return Capture(startIndex, operations.lastIndex)
    }
}