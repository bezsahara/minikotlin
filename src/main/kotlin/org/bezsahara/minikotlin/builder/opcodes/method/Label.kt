package org.bezsahara.minikotlin.builder.opcodes.method

enum class LabelType {
    ControlFlow,
    MetadataOnly,
    Unknown,
    CatchBlockOnly
}


fun th() {
    try {
        println("st")
        val k = StringBuilder()
    } catch (e: Error) {
        println("end")
    }
}

class Label(var name: String?) {
    constructor() : this(null)
    var labelType: LabelType = LabelType.Unknown
        private set

    fun markAsCatchBlock() {
        labelType = LabelType.CatchBlockOnly
    }

    internal fun markAsControlFlowLabel() {
        if (labelType != LabelType.Unknown) return

        labelType = LabelType.ControlFlow
    }

    fun markAsMeta() {
        if (labelType != LabelType.Unknown) return

        labelType = LabelType.MetadataOnly
    }

    val itsID: Long = counterId.getAndAdd(1)

    override fun toString(): String {
        return if (name == null) {
            "L[$itsID] $labelType"
        } else "L($name)[$itsID] $labelType"
    }

    companion object {
        private val counterId = java.util.concurrent.atomic.AtomicLong(0)

        @JvmStatic
        fun resetCounterId(newValue: Long) {
            counterId.set(newValue)
        }
    }
}