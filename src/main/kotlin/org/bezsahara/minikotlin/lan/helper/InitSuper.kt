package org.bezsahara.minikotlin.lan.helper

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.ext.autoInit
import org.bezsahara.minikotlin.builder.opcodes.ext.autoInitAndReturn
import org.bezsahara.minikotlin.lan.KValue
import org.bezsahara.minikotlin.lan.StackInfo

class InitSuper : KValue.ValueBlock(null) {
    override fun KBMethod.init(
        variables: Map<String, Int>,
        stackInfo: StackInfo,
    ) {
        autoInit()
    }
}