package org.bezsahara.minikotlin.verifiertest

import org.bezsahara.minikotlin.builder.ClassProperties
import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.declaration.DeclarationProperty
import org.bezsahara.minikotlin.builder.declaration.ThisClassInfo
import org.bezsahara.minikotlin.builder.opcodes.ext.*
import org.bezsahara.minikotlin.builder.opcodes.method.Label
import org.bezsahara.minikotlin.compiler.verifier.KBOpcodesVerifier
import org.bezsahara.minikotlin.compiler.verifier.v.VerifierVariableUninitializedException
import org.bezsahara.minikotlin.compiler.verifier.v.VerifierVariableWrongKindException
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows

/**
 * Verifier‑error “smoke tests”.
 *
 * ──────────────────────────────────────────────────────────────────────────────
 *  Exception types covered
 *  ──────────────────────────────────
 *  VerifierVariableUninitializedException          →  tests #1, #2, #8
 *  VerifierVariableWrongKindException
 *        isW64Occupation == false (kind clash)     →  tests #3, #4, #7
 *        isW64Occupation == true  (cat‑2 overlap)  →  tests #5, #6
 * ──────────────────────────────────────────────────────────────────────────────
 */
object VerifierVariableTests {

    /* ---------- helpers ---------------------------------------------------- */

    private fun newMethod() =
        KBMethod("test", DeclarationProperty.voidType.copy(isStatic = true), ClassProperties.Default)

    private fun verifier(res: KBMethod.Result) = KBOpcodesVerifier(
        ClassProperties.Default,
        res.operations,
        Void.TYPE,
        res.parameters,
        res.methodProperty.isStatic,
        res.name,
        ThisClassInfo.withAutoShadow("none")
    )

    /* ---------- 1 ─ Uninitialised simple load ----------------------------- */
    @Test
    fun `uninitialised direct load`() {
        val res = newMethod().apply {
            iload(0)              // ← never initialised
        }.result()

        assertThrows<VerifierVariableUninitializedException> {
            verifier(res).run()
        }
    }

    /* ---------- 2 ─ Uninitialised after merge ----------------------------- */
    @Test
    fun `uninitialised after divergent paths`() {
        val merge = Label("merge")
        val res = newMethod().apply {
            iconst_0()
            ifne(merge)           // branch A (does NOT init var 1)
            ldc(7)
            istore(1)             // branch B (inits var 1)
            goto(merge)

            labelPoint(merge)
            iload(1)              // ← only safe if var 1 set on all paths
        }.result()

        val k = assertThrows<VerifierVariableUninitializedException> {
            verifier(res).run()
        }
//        println(k.message)
    }

    /* ---------- 3 ─ Kind clash on same path ------------------------------- */
    @Test
    fun `kind clash same path`() {
        val res = newMethod().apply {
            iconst_1(); istore(0)   // int
            dconst_0(); dstore(0)   // overwrite with double
            iload(0)                // tries to read as int again
        }.result()

        val ex = assertThrows<VerifierVariableWrongKindException> {
            verifier(res).run()
        }
        assertFalse(ex.isW64Occupation)          // not a cat‑2 overlap ‑ just wrong kind
    }

    /* ---------- 4 ─ Kind clash across branches ---------------------------- */
    @Test
    fun `kind clash across divergent paths`() {
        val join = Label("join")
        val res = newMethod().apply {
            iconst_0(); ifne(join)               // branch A
            iconst_5(); istore(2); goto(join)    // var2 = int
            labelPoint(join)
            aconst_null(); astore(2)             // branch B stores reference
            iload(2)                             // read as int → mismatch with ref
        }.result()

        val ex = assertThrows<VerifierVariableWrongKindException> {
            verifier(res).run()
        }
        assertFalse(ex.isW64Occupation)
    }

    /* ---------- 5 ─ Cat‑2 overlap  (existing sample) ---------------------- */
    @Test
    fun `cat2 overlap forward`() {
        val res = newMethod().apply {
            val exit = label("exit")
            ldc(2.0); dstore(1)      // slots 1 & 2
            goto(exit)
            labelPoint(exit)
            iconst_0(); istore(2)    // ← writes into second half
        }.result()

        val ex = assertThrows<VerifierVariableWrongKindException> {
            verifier(res).run()
        }
        assertTrue(ex.isW64Occupation)
    }

    /* ---------- 6 ─ Cat‑2 overlap other half ------------------------------ */
    @Test
    fun `cat2 overlap backward`() {
        val res = newMethod().apply {
            dconst_0(); dstore(3)    // occupies 3 & 4
            iconst_1(); istore(4)    // writes into second half
        }.result()

        val ex = assertThrows<VerifierVariableWrongKindException> {
            verifier(res).run()
        }
        assertTrue(ex.isW64Occupation)
    }

    /* ---------- 7 ─ Three‑way merge with two matching, one clashing -------- */
    @Test
    fun `three‑way merge wrong kind`() {
        val p1 = Label("P1");
        val p2 = Label("P2");
        val join = Label("JOIN")
        val res = newMethod().apply {
            iconst_0(); ifne(p1)           // Path 1
            goto(p2)

            labelPoint(p1)                 // int store
            ldc(9); istore(6); goto(join)

            labelPoint(p2)                 // ref store
            aconst_null(); astore(6); goto(join)

            labelPoint(join)
            iload(6)                       // type clash on use
        }.result()

        val ex = assertThrows<VerifierVariableWrongKindException> {
            verifier(res).run()
        }
        assertFalse(ex.isW64Occupation)
    }

    /* ---------- 8 ─ Uninitialised via table‑like split (hard) ------------- */
    @Test
    fun `uninitialised switch style`() {
        val l0 = Label("L0");
        val l1 = Label("L1");
        val done = Label("DONE")
        val res = newMethod().apply {
            iconst_0(); ifne(l0)           // jump to either L0 or fall‑through

            // fall‑through path (does NOT set var 4)
            goto(l1)

            labelPoint(l0)
            ldc(123); istore(4)            // var 4 initialised here
            goto(done)

            labelPoint(l1)
            // no initialisation

            labelPoint(done)
            iload(4)                       // unsafe read
        }.result()

        assertThrows<VerifierVariableUninitializedException> {
            verifier(res).run()
        }
    }
}