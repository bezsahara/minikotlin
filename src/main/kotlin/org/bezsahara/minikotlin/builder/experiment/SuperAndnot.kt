package org.bezsahara.minikotlin.builder.experiment

open class A { open fun test() {} }
class B : A()
fun accept(b: A) { b.test() }

fun ro() { accept(B()) }