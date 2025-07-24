package org.bezsahara.minikotlin.lan

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.KBClass
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.ThisMethod
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.lan.KVar.Obj
import org.bezsahara.minikotlin.lan.compiler.MiniKotlinCompiler
import org.bezsahara.minikotlin.lan.logic.ErrorHandler
import org.bezsahara.minikotlin.lan.logic.PropertyGet
import org.bezsahara.minikotlin.lan.logic.PropertySet
import org.bezsahara.minikotlin.lan.logic.ReturnPiece
import org.bezsahara.minikotlin.lan.logic.ThrowValue
import org.bezsahara.minikotlin.lan.pieces.ActionPiece
import org.bezsahara.minikotlin.lan.pieces.CustomActionPiece
import org.bezsahara.minikotlin.lan.pieces.VariableSet
import kotlin.reflect.*
import kotlin.reflect.jvm.ExperimentalReflectionOnLambdas
import kotlin.reflect.jvm.javaField
import kotlin.reflect.jvm.javaGetter
import kotlin.reflect.jvm.javaSetter

typealias MiniKotlinAny = MiniKotlin<out Any>


class MiniKotlin<F: Any>(
    private val thisClassName: String,
    private val static: Boolean,
    val classProperties: ClassProperties,
    val kbClass: KBClass,
    val params: List<KBMethod.Parameter>,
    val methodProperty: DeclarationProperty<*,*>,
    val methodName: String
) {
    val thisFun = ThisFun<F>(this)

    internal val variables = hashMapOf<String, KVar<*>>()


    var indexId: Int = 0
        private set
    private fun getIndex(): Int {
        return ++indexId
    }

    private val actionPieces = arrayListOf<ActionPiece>()

    fun immutableCodePieces(): List<ActionPiece> {
        return actionPieces
    }

    // Work around for if else

    abstract class BeforeNextAdd {
        abstract fun wasDiscarded()

        abstract fun getCodePieces(indexOfFirst: Int): List<ActionPiece>

        // If false the whole thing is discarded
        abstract fun isFineToAdd(nextPiece: ActionPiece): Boolean
    }

    class BeforeNextAddHolder(val beforeNextAdd: BeforeNextAdd, val index: Int)

    private var beforeAdd: BeforeNextAddHolder? = null

    fun beforeNextAddThis(beforeNextAdd: BeforeNextAdd) {
        require(beforeAdd == null) { "beforeAdd must be null to have a new value!" }
        beforeAdd = BeforeNextAddHolder(beforeNextAdd, 0)
    }

    private var codePiecesIndex = 0
    fun addPiece(p: ActionPiece): Int {
        beforeAdd?.let {
            beforeAdd = null
            val bna = it.beforeNextAdd
            if (bna.isFineToAdd(p)) {
                bna.getCodePieces(codePiecesIndex + 1).forEach { codePiece ->
                    actionPieces.add(codePiece)
                    codePiecesIndex += 1
                }
            }
            bna.wasDiscarded()
        }
        actionPieces.add(p)
        return codePiecesIndex++
    }

    private val thisInner = KVar.Obj<Any>(thisClassName, Any::class, KValue.NotPresent)

    val this_ get() = if (static) error("Cannot get static") else thisInner

    @Suppress("FunctionName", "UNREACHABLE_CODE")
    fun throw_(r: KRef<out Throwable>) {
        addPiece(CustomActionPiece(ThrowValue(r)))
    }

    // Returns

    @Suppress("FunctionName", "UNREACHABLE_CODE")
    fun return_() {
        addPiece(CustomActionPiece(ReturnPiece(null)))
    }

    // Variables

    private var varNameIndex = 0

    fun createVariableName(): String {
        var name = "av$varNameIndex"
        while (variables.containsKey(name)) {
            varNameIndex += 1
            name = "av$varNameIndex"
        }
        return name
    }

    fun <T: Any> variable(name: String, clazz: KClass<T>): KVar.Obj<T> {
        var variableInMap = variables[name]
        if (variableInMap == null) {
            variableInMap = KVar.Obj(name, clazz)
            variables[name] = variableInMap
        }
        return if (variableInMap is KVar.Obj<*>) {
            if (!variableInMap.jClass.isAssignableFrom(clazz.javaObjectType)) {
                error("Type of variable $name is not assignable to $clazz")
            }
            variableInMap as KVar.Obj<T>
        } else {
            error("Type of variable $name is not object")
        }
    }

    inline fun <reified T: Any> variable(name: String): KVar.Obj<T> {//KVar.Obj<T> {
        return variable(name, T::class)
    }

    init {
        val offset = if (methodProperty.isStatic) 0 else 1
        if (!methodProperty.isStatic) {
            variables["this"] = KVar.Obj<Any>("this", Any::class, KValue.NotPresent, forcedIndex = 0)
        }
        params.forEachIndexed { index, it ->
            val jClass = it.typeInfo.recoverJClass() ?: Any::class.java



            variables[it.name] =
                if (jClass.isPrimitive) {
                    KVar.Native<Any>(it.name, jClass.kotlin, KValue.NotPresent, forcedIndex = it.index)
                } else {
                    KVar.Obj<Any>(it.name, jClass.kotlin, KValue.NotPresent, forcedIndex = it.index)
                }.also { it.initialized = true }
        }
    }

    fun <T: Any> variableNt(name: String, clazz: KClass<T>): KVar.Native<T> {
        var variableInMap = variables[name]
        if (variableInMap == null) {
            variableInMap = KVar.Native<T>(name, clazz, KValue.NotPresent)
            variables[name] = variableInMap
        }
        if (variableInMap is KVar.Native) {
            if (!variableInMap.jClass.isAssignableFrom(clazz.javaPrimitiveType ?: error("Not a primitive type!"))) {
                error("Type of variable $name is not assignable to $clazz")
            }
            return variableInMap as KVar.Native<T>
        } else {
            error("Type of variable $name is not native")
        }
    }

    inline fun <reified T: Any> variableNt(name: String): KVar.Native<T> {
        return variableNt(name, T::class)
    }

    // setTo

    infix fun <T: Any> KVar.Obj<T>.setTo(other: KRef.Obj<out T>): KVar.Obj<T> {
        initialized = true
        addPiece(VariableSet(name, this, other.use()))
        return this
    }

    infix fun <T: Any> KVar.Native<T>.setTo(other: KRef.Native<out T>): KVar.Native<T> {
        initialized = true
        addPiece(VariableSet(name, this, other.use()))
        return this
    }

    // Fields and related

    private fun <T: Any, V: Any> propertyGetP(p: KProperty1<T, V?>, arg: KRef.Obj<out T>, asNative: Boolean): KRef<V> {
//        val idx = getIndex()
        val asGetter = p.javaGetter
        // It is a getter
        val i2 = if (asGetter != null) {
            PropertyGet(
                p,
                TypeInfo.Java(asGetter.declaringClass),
                asGetter.name,
                TypeInfo.Java(asGetter.returnType),
                arg,
                if (asGetter.declaringClass.isInterface) {
                    15
                } else {
                    14
                },
                asNative
            )
        } else {
            // it is field
            val asField = p.javaField!!
            PropertyGet(p,
                TypeInfo.Java(asField.declaringClass),
                asField.name,
                TypeInfo.Java(asField.type),
                arg,
                24,
                asNative
            )
        }
        return if (!asNative) {
            KRef.Obj<V>(p.returnType.getKClass(),
                i2)
        } else {
            KRef.Native<V>(p.returnType.getKClass(),
                i2)
        }
    }

    private fun <V: Any> propertyGetP(p: KProperty0<V?>, asNative: Boolean): KRef<V> {
        val asGetter = p.javaGetter
        // It is a getter
        val i = if (asGetter != null) {
            val dc = asGetter.declaringClass
            val objectRef = dc.kotlin.objectInstance?.javaClass
            PropertyGet(p,
                TypeInfo.Java(dc),
                asGetter.name,
                TypeInfo.Java(asGetter.returnType),
                objectRef?.let { KRef.Obj(it.kotlin, objectInstanceGet(TypeInfo.Java(it))).use() },
                if (objectRef != null) 14 else 13,
                asNative
            )
        } else {
            // it is field
            val asField = p.javaField!!
            val dc = asField.declaringClass
            val objectRef = dc.kotlin.objectInstance?.javaClass
            PropertyGet(p,
                TypeInfo.Java(asField.declaringClass),
                p.name,
                TypeInfo.Java(asField.type),
                objectRef?.let { KRef.Obj(it.kotlin, objectInstanceGet(TypeInfo.Java(it))).use() },
                if (objectRef != null) 24 else 23,
                asNative
            )
        }
        return if (!asNative) {
            KRef.Obj(
                (i.returnType as TypeInfo.JClassAvailable).jClass as Class<V>,
                i
            )
        } else {
            KRef.Native(p.returnType.getKClass(), i)
        }
    }

    fun <V: Any> propertyGet(p: KProperty0<V?>): KRef.Obj<V> {
        return propertyGetP(p, false) as KRef.Obj<V>
    }

    fun <T: Any, V: Any> propertyGet(p: KProperty1<T, V?>, arg: KRef.Obj<out T>): KRef.Obj<V> {
        return propertyGetP(p, arg, false) as KRef.Obj<V>
    }

    fun <V: Any> propertyGetNt(p: KProperty0<V?>): KRef.Native<V> {
        return propertyGetP(p, true) as KRef.Native<V>

    }

    fun <T: Any, V: Any> propertyGetNt(p: KProperty1<T, V?>, arg: KRef.Obj<out T>): KRef.Native<V> {
        return propertyGetP(p, arg, true) as KRef.Native<V>
    }

    // sets

    fun <V: Any> propertySet(p: KMutableProperty0<V>, v: KRef.Obj<V>) {
        addPiece(CustomActionPiece(PropertySet(p, p.name, v.use(), false, null)))
    }

    fun <V: Any> propertySetNt(p: KMutableProperty0<V>, v: KRef.Native<V>) {
        addPiece(CustomActionPiece(PropertySet(p, p.name, v.use(), true, null)))
    }

    fun <V: Any, A: Any> propertySetNt(p: KMutableProperty1<A, V>, arg: KRef.Obj<A>, v: KRef.Native<V>) {
        addPiece(CustomActionPiece(PropertySet(p, p.name, v.use(), true, arg.use())))
    }

    fun <V: Any, A: Any> propertySet(p: KMutableProperty1<A, V>, arg: KRef.Obj<A>, v: KRef.Obj<V>) {
        addPiece(CustomActionPiece(PropertySet(p, p.name, v.use(), false, arg.use())))
    }

    @Suppress("FunctionName")
    inline fun try_(block: () -> Unit): ErrorHandler.CatchBlock {
        return ErrorHandler(this).let {
            block()
            it.endOfTry()
        }
    }

    @Suppress("FunctionName")
    inline fun if_(condition: KRef.Native<Boolean>, block: () -> Unit): IfBlock.Else {
        return IfBlock(this).if_(condition, block)
    }


    inline fun switch(block: Switch.Scope.() -> Unit) {
        Switch(this).apply {
            makeScope().block()
            finish()
        }
    }

    inline fun switch(number: KRef.Native<Int>, block: TableOrLookupSwitch.() -> Unit) {
        TableOrLookupSwitch(this, number).apply {
            block()
            execute()
        }
    }

    fun rt(a:Int): String = ""

    inline fun <reified T: Any> createRefNt(ci: KValue.VBReturns): KRef.Native<T> {
        return KRef.Native(T::class, ci)
    }

    fun performAction(ci: KValue.ValueBlock) {
        addPiece(CustomActionPiece(ci))
    }
}

@JvmName("runsMiniKtT")
infix fun <T: Function<*>> KBMethod.Builder<T>.runsMiniKt(block: MiniKotlin<T>.() -> Unit): ThisMethod<T> {
    val mk = MiniKotlin<T>("", false, classProperties, kbClass, this.params, methodProperty, name)
    mk.block()
    return runs { MiniKotlinCompiler(mk.immutableCodePieces(), mk.variables, this).compile() } as ThisMethod<T>
}

infix fun KBMethod.Builder<Any>.runsMiniKt(block: MiniKotlin<Any>.() -> Unit): ThisMethod<out Any> {
    val mk = MiniKotlin<Any>("", false, classProperties, kbClass, this.params, methodProperty, name)
    mk.block()
    return runs { MiniKotlinCompiler(mk.immutableCodePieces(), mk.variables, this).compile() }
}

fun main() {
    val k =0
    var lap = { l: String, r: Int ->
        println(k)
    }
    //INVOKEDYNAMIC invoke(I)Lkotlin/jvm/functions/Function2; [
    //      // handle kind 0x6 : INVOKESTATIC
    //      java/lang/invoke/LambdaMetafactory.metafactory(
    //      Ljava/lang/invoke/MethodHandles$Lookup;
    //      Ljava/lang/String;Ljava/lang/invoke/MethodType;
    //      Ljava/lang/invoke/MethodType;Ljava/lang/invoke/MethodHandle;
    //      Ljava/lang/invoke/MethodType;)Ljava/lang/invoke/CallSite;
    //      // arguments:
    //      (Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;,
    //      // handle kind 0x6 : INVOKESTATIC
    //      org/bezsahara/kbytes/lan/LanguageKt.main$lambda$2(ILjava/lang/String;I)Lkotlin/Unit;,
    //      (Ljava/lang/String;Ljava/lang/Integer;)Lkotlin/Unit;
    //    ]
//    MethodHandle::invoke
//    java/lang/invoke/ LambdaMetafactory.metafactory
    lap = { l: String, r: Int ->
        tyu + k
    }
    printTypeInfo(lap)
    lap.invoke("", 2)
    lap("", k)

//    ltt(k = 0, lap)
}
fun printTypeInfo(obj: Any) {
    val clazz = obj.javaClass
    println("Class: ${clazz.name}")
    println("Superclass chain:")
    var current: Class<*>? = clazz
    while (current != null) {
        println("  -> ${current.name}")
        current = current.superclass
    }

    println("Interfaces:")
    clazz.interfaces.forEach { iface ->
        println("  - ${iface.name}")
    }
}
fun toop() {}

var tyu = 9

//@OptIn(ExperimentalReflectionOnLambdas::class)
//fun ltt(k: Int, a: () -> Unit) {
//
//    println((a as? KFunction<*>))
//}

fun addition(a: Int): Int {
    error("")
}

object KotlinObject {
    fun noArgFun() {

    }
}

var sys = 0


@OptIn(ExperimentalReflectionOnLambdas::class)
fun mai33333n() {
    val k = ::sys
    println(k.javaGetter)
    println(k.javaSetter)
}