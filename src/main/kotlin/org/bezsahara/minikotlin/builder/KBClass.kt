package org.bezsahara.minikotlin.builder

import com.sun.source.util.Plugin
import org.bezsahara.minikotlin.builder.auto.KeyStoreInfo
import org.bezsahara.minikotlin.builder.auto.insertAutoInitForKBS
import org.bezsahara.minikotlin.builder.declaration.*
import org.bezsahara.minikotlin.builder.opcodes.ext.autoInit
import org.bezsahara.minikotlin.builder.opcodes.method.LabelPoint
import org.bezsahara.minikotlin.builder.plugin.PluginData
import org.bezsahara.minikotlin.builder.plugin.PluginKey
import org.bezsahara.minikotlin.compiler.KBClassLoader
import org.bezsahara.minikotlin.compiler.KBClassPair
import org.bezsahara.minikotlin.compiler.KBCompiler
import org.bezsahara.minikotlin.compiler.asm.KBCompilerASM
import org.bezsahara.minikotlin.compiler.asm.mapA
import org.bezsahara.minikotlin.compiler.verifier.KBOpcodesVerifier
import org.bezsahara.minikotlin.lan.logic.bridge.KeyStorage0
import org.objectweb.asm.Opcodes
import java.io.File
import java.util.IdentityHashMap
import kotlin.jvm.internal.ClassBasedDeclarationContainer
import kotlin.reflect.KClass
import kotlin.reflect.KFunction
import kotlin.reflect.KParameter
import kotlin.reflect.jvm.javaMethod

//[Annotations] [Visibility]
// [abstract|static|default|final|synchronized|native|strictfp]
// returnType methodName(...)

enum class Visibility(val asmOpcode: Int) {
    Private(Opcodes.ACC_PRIVATE),
    Protected(Opcodes.ACC_PROTECTED),
    Public(Opcodes.ACC_PUBLIC),
    None(0)
}

class KBClass(
    private val implementing: List<TypeInfo>,
    val name: String,
    val classProperties: ClassProperties,
) {
    class Result(
        val name: String,
        val interfaces: List<TypeInfo>,
        val methodsResult: List<KBMethod.Result>,
        val fields: List<KBField>,
        val compiler: KBCompiler,
        val classProperties: ClassProperties,
        val thisClassInfo: ThisClassInfo
    ) {

        fun saveToFolder(path: String) {
            File(path, "$name.class").writeBytes(toByteArray())
        }

        private var classByteCode: ByteArray? = null

        fun printCode(appendable: Appendable = System.out) {
            methodsResult.forEach {
                var numIndex = 0
                val ppp = it.parameters.joinToString(prefix = "(", postfix = ")") { it.typeInfo.getReturnStringRep() }
                appendable.appendLine($$"$ --- Function: $${it.methodProperty} $${it.name}$$ppp")
                it.operations.forEachIndexed { index, op ->
                    val unPacked = op.actual()
                    if (unPacked !is LabelPoint) {
                        appendable.append(numIndex.toString().padStart(3))
                        appendable.append("  ")
                    } else {
                        appendable.append("   ")
                    }
                    numIndex++
                    appendable.appendLine(unPacked.toString())
                }
            }
        }

        private fun verify() {
            methodsResult.forEach {
                val jClass = it.methodProperty.typeInfo!!.recoverJClass()
                KBOpcodesVerifier(
                    classProperties,
                    it.operations,
                    jClass,
                    it.parameters,
                    it.methodProperty.isStatic,
                    it.name,
                    thisClassInfo
                ).apply {
                    run()
                }
            }
        }

        fun toByteArray(): ByteArray {
            classByteCode?.let { return it }
            if (classProperties.verifier) {
                verify()
            }
            val cp = compiler.compileClass(this)
            classByteCode = cp
            return cp
        }

        fun <T> initAndGetAsInterface(clazzInterface: Class<T>, parent: ClassLoader): T {
            val clazz = loadClass(parent).clazz
            if (clazzInterface.isAssignableFrom(clazz)) {
                val cn = clazz.constructors.firstOrNull { it.parameters.size == 0 }
                if (cn == null) {
                    error("A zero param constructor was expected")
                }
                return cn.newInstance() as T
            } else {
                error("Your class is not a subtype of specified interface")
            }
        }

        inline fun <reified T> initAndGetAsInterface(parent: ClassLoader = T::class.java.classLoader): T {
            return initAndGetAsInterface(T::class.java, parent)
        }

        fun loadClass(parent: ClassLoader? = this::class.java.classLoader): KBClassPair {
            val cl = KBClassLoader(parent)
            val clazz = cl.define(name, toByteArray())
            return KBClassPair(
                cl,
                clazz
            )
        }
    }

    val idOfKey = KeyStorage0.createNewKey()

    val keyNamesMap = hashMapOf<String, KeyStoreInfo>()
    private var staticFieldNaming = 0

    fun insertNewEntryToMap(i: Int, typeInfo: TypeInfo): String {
        val name = "${classProperties.syntheticKey}f$staticFieldNaming"
        staticFieldNaming++
        require(!keyNamesMap.containsKey(name)) { "This name $name already exists in map" }
        keyNamesMap[name] = KeyStoreInfo(i, typeInfo, true)
        return name
    }

    // TODO create better system for code insertion
    fun result(compiler: KBCompiler = KBCompilerASM()): Result {
        require(methods.containsKey("<init>"))

        val sNotEmpty = keyNamesMap.isNotEmpty()
        if (sNotEmpty && !methods.containsKey("<clinit>")) {
            static()
        }
        val builtMethods = methods.values.map { it.build() }
        if (sNotEmpty) {
            val m = builtMethods.firstOrNull { it.name == "<clinit>" }!!

            insertAutoInitForKBS(m, this)
        }

        return Result(
            name,
            implementing,
            builtMethods.mapTo(mutableListOf()) { it.result() },
            fields.toList(),
            compiler,
            classProperties,
            ThisClass
        ).also {
            KeyStorage0.getKBKey(idOfKey).build()
        }
    }

    class Builder(val name: String, val classProperties: ClassProperties) {
        val implementing = mutableListOf<TypeInfo>()

        infix fun implements(other: KClass<out Any>): Builder {
            implementing.add(TypeInfo.Kt(other))
            return this
        }

        infix fun implements(other: Class<out Any>): Builder {
            implementing.add(TypeInfo.Java(other))
            return this
        }

        fun implementsAll(vararg interfaces: KClass<out Any>): Builder {
            implementing.addAll(interfaces.map { TypeInfo.Kt(it) })
            return this
        }

        inline infix fun body(block: KBClass.() -> Unit): KBClass {
            return KBClass(implementing, name, classProperties).also(block)
        }
    }

    fun static(): KBMethod.Builder<Function<Unit>> {
        return (static ofType TypeInfo.Void).methodTyped<Function<Unit>>("<clinit>")
    }

    // TODO allow to put methods that have different signatures but same names
    val methods = hashMapOf<String, KBMethod.Builder<*>>()

    fun addMethod(methodKey: String, builder: KBMethod.Builder<*>) {
        if (methodKey in methods) {
            error("You already defined function named $methodKey")
        }

        methods.put(methodKey, builder)
    }

    private val fields = mutableListOf<KBField>()


    val public = DeclarationProperty.public
    val private = DeclarationProperty.private
    val protected = DeclarationProperty.protected

    val final = DeclarationProperty.final
    val abstract = DeclarationProperty.abstract
    val native = DeclarationProperty.native
    val static = DeclarationProperty.static
    val default = DeclarationProperty.default
    val synchronized = DeclarationProperty.synchronized
    val strictfp = DeclarationProperty.strictfp

    val volatile = DeclarationProperty.volatile
    val transient = DeclarationProperty.transient

    @Suppress("PropertyName")
    val ThisClass = ThisClassInfo.withAutoShadow(name, implementing.mapA { it.getStringRep() })

    operator fun String.invoke(vararg params: Pair<String, TypeInfo>): KBClass.StringWithParam {
        return KBClass.StringWithParam(this) { or ->
            var idx = 0
            params.map {
                val bef = idx
                val isDouble = it.second.occupiesTwo()
                idx += if (isDouble) 2 else 1
                KBMethod.Parameter(it.first, it.second, bef, isDouble)
            }
        }
    }

    class StringWithParam(
        val name: String,
        val paramsFactory: (Int) -> List<KBMethod.Parameter>,
    )

    fun init(block: KBMethod.() -> Unit) {
        public ofType TypeInfo.Void method "<init>" runs (block)
    }

    val pluginMap = IdentityHashMap<PluginKey, PluginData>()

    // Auto inits the init. It just calls init of the Object
    fun autoInit(block: KBMethod.() -> Unit = {}) {
        init {
            block()
            autoInit()
        }
    }

    fun <T : Function<*>> implOf(
        fj: T,
    ): KBMethod.Builder<T> {
        return implOf(fj, true, null)
    }

    fun <T : Function<*>> implOf(
        fj: T,
        final: Boolean = true,
        extraDP: DeclarationProperty<DP.CanBeMethod, TypeInfo>? = null,
    ): KBMethod.Builder<T> {
        val f = (fj as KFunction<*>)
        val rt = f.javaMethod?.returnType ?: error("Function you provided is not javaMethod")
        val typeInfo = TypeInfo.Java(rt)

        val valueParams = f.parameters
            .filter {
                it.kind == KParameter.Kind.VALUE || it.kind == KParameter.Kind.EXTENSION_RECEIVER
            }


        var pIndex = 1
        val parameters = valueParams.mapIndexed { index, param ->
            val name = param.name ?: if (param.kind == KParameter.Kind.EXTENSION_RECEIVER) {
                "ext$index"
            } else {
                "arg$index"
            }
            val pClass = (param.type.classifier as? ClassBasedDeclarationContainer)?.jClass ?: Any::class.java
            val pPre = pIndex
            val buff = when (pClass) {
                Double::class.java, Long::class.java -> 2
                else -> 1
            }
            pIndex += buff
            KBMethod.Parameter(
                name = name,
                typeInfo = TypeInfo.Java(pClass),
                index = pPre,
                buff == 2
            )
        }

        val declProp = DeclarationProperty<DP.CanBeMethod, TypeInfo>(
            visibility = Visibility.Public,
            typeInfo = typeInfo,
            isFinal = final
        ).let {
            if (extraDP != null) {
                it.combine(extraDP)
            } else {
                it
            }
        }

        return KBMethod.Builder<T>(f.name, declProp, parameters, classProperties, this)
            .also { methods.put(f.name, it) }
    }

    // Better just use different names
    infix fun DeclarationProperty<out DP.CanBeMethod, out TypeInfo>.method(name: String): KBMethod.Builder<Any> {
        return KBMethod.Builder<Any>(name, this, classProperties = classProperties, kbClass = this@KBClass)
            .also { addMethod(name, it) }
    }

    fun <T : Function<*>> DeclarationProperty<out DP.CanBeMethod, out TypeInfo>.methodTyped(name: String): KBMethod.Builder<T> {
        return KBMethod.Builder<T>(name, this, classProperties = classProperties, kbClass = this@KBClass)
            .also { addMethod(name, it) }
    }

    infix fun DeclarationProperty<out DP.CanBeMethod, out TypeInfo>.method(stringWithParam: StringWithParam): KBMethod.Builder<Any> {
        val params = stringWithParam.paramsFactory(if (isStatic) 0 else 1)
        return KBMethod.Builder<Any>(
            stringWithParam.name,
            this,
            params,
            classProperties,
            kbClass = this@KBClass
        ).also { addMethod(stringWithParam.name, it) }
    }

    infix fun DeclarationProperty<out DP.CanBeField, out TypeInfo>.field(name: String): ThisField {
        KBField(name, this).also { fields.add(it) }
        return ThisField(name, this.typeInfo!!, isStatic)
    }
}

fun makeClass(name: String, classProperties: ClassProperties = ClassProperties.Default): KBClass.Builder {
    return KBClass.Builder(name, classProperties)
}