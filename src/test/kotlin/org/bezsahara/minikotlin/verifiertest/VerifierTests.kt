package org.bezsahara.minikotlin.verifiertest

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.ThisClassInfo
import org.bezsahara.minikotlin.builder.declaration.TypeInfo
import org.bezsahara.minikotlin.builder.declaration.args
import org.bezsahara.minikotlin.builder.declaration.typeInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.compiler.verifier.KBOpcodesVerifier
import org.bezsahara.minikotlin.compiler.verifier.VerifierStackException
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import kotlin.test.assertTrue

object VerifierTests {
    private fun newMethod() = KBMethod("test", DeclarationProperty.voidType, ClassProperties.Default)
    private fun verifier(res: KBMethod.Result) = KBOpcodesVerifier(
        ClassProperties.Default,
        res.operations,
        Void.TYPE,
        res.parameters,
        res.methodProperty.isStatic,
        res.name,
        ThisClassInfo.withAutoShadow("none")
    )

    @Test
    fun `verifier must throw`() {
        val m = newMethod().apply {
            ldc(2.0) // load D
            ldc(1) // load I
            swap() // must throw

            return_()
        }.result()

        assertThrows<VerifierStackException> { verifier(m).run() }
    }

    @JvmStatic
    fun accept(c: CharSequence) {
        println(c)
    }

    @Test
    fun `types differ`() {
        val m = newMethod().apply {
            ldc(TypeInfo.IntObj)
            invokestatic(typeInfo<VerifierTests>(), "accept", args(CharSequence::class))

            return_()
        }.result()
        val thrown = assertThrows<VerifierStackException> { verifier(m).run() }
    }

    @Test
    fun `verifier must throw different stack`() {
        val m = newMethod().apply {
            val someLabel = label("s")
            ldc(0)
            ifne(someLabel)

            ldc(2.0) // load D
            labelPoint(someLabel)

            return_()
        }.result()
        val thrown = assertThrows<VerifierStackException> { verifier(m).run() }
        assertTrue(thrown.stackDifference)
    }

    @Test
    fun `verifier wrong swap`() {
        val m = newMethod().apply {
            ldc(2.toShort())
            ldc(2.0)
            swap()

            return_()
        }.result()
        val thrown = assertThrows<VerifierStackException> { verifier(m).run() }
    }

    class TestClass

    @Test
    fun `wrong kinds`() {
        val m = newMethod().apply {
            ldc(2)
            istore(0)

            newAndInit(typeInfo<TestClass>())
            astore(1)

            aload(1)
            invokestatic(typeInfo<VerifierTests>(), "accept", args(CharSequence::class))

            return_()
        }.result()
//        verifier(m).run()
        val thrown = assertThrows<VerifierStackException> { verifier(m).run() }
//        assertTrue(thrown.stackDifference)
    }
}

