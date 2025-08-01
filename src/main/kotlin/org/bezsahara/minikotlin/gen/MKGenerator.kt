package org.bezsahara.minikotlin.gen

import org.bezsahara.minikotlin.builder.KBClass

abstract class MKGenerator {
    abstract fun generateClass(): List<KBClass.Result>
}