@file:Suppress("FunctionName", "FunctionName", "SpellCheckingInspection")

package org.bezsahara.minikotlin.builder.opcodes.ext

import org.bezsahara.minikotlin.builder.KBMethod
import org.bezsahara.minikotlin.builder.opcodes.codes.InsnOp
import org.bezsahara.minikotlin.builder.opcodes.method.KBSingleOP


fun KBMethod.nop()            { addOperation(KBSingleOP.createOrGet(InsnOp.NOP)) }
fun KBMethod.aconst_null()    { addOperation(KBSingleOP.createOrGet(InsnOp.ACONST_NULL)) }
fun KBMethod.iconst_m1()      { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_M1)) }
fun KBMethod.iconst_0()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_0)) }
fun KBMethod.iconst_1()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_1)) }
fun KBMethod.iconst_2()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_2)) }
fun KBMethod.iconst_3()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_3)) }
fun KBMethod.iconst_4()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_4)) }
fun KBMethod.iconst_5()       { addOperation(KBSingleOP.createOrGet(InsnOp.ICONST_5)) }
fun KBMethod.lconst_0()       { addOperation(KBSingleOP.createOrGet(InsnOp.LCONST_0)) }
fun KBMethod.lconst_1()       { addOperation(KBSingleOP.createOrGet(InsnOp.LCONST_1)) }
fun KBMethod.fconst_0()       { addOperation(KBSingleOP.createOrGet(InsnOp.FCONST_0)) }
fun KBMethod.fconst_1()       { addOperation(KBSingleOP.createOrGet(InsnOp.FCONST_1)) }
fun KBMethod.fconst_2()       { addOperation(KBSingleOP.createOrGet(InsnOp.FCONST_2)) }
fun KBMethod.dconst_0()       { addOperation(KBSingleOP.createOrGet(InsnOp.DCONST_0)) }
fun KBMethod.dconst_1()       { addOperation(KBSingleOP.createOrGet(InsnOp.DCONST_1)) }

fun KBMethod.iaload()         { addOperation(KBSingleOP.createOrGet(InsnOp.IALOAD)) }
fun KBMethod.laload()         { addOperation(KBSingleOP.createOrGet(InsnOp.LALOAD)) }
fun KBMethod.faload()         { addOperation(KBSingleOP.createOrGet(InsnOp.FALOAD)) }
fun KBMethod.daload()         { addOperation(KBSingleOP.createOrGet(InsnOp.DALOAD)) }
fun KBMethod.aaload()         { addOperation(KBSingleOP.createOrGet(InsnOp.AALOAD)) }
fun KBMethod.baload()         { addOperation(KBSingleOP.createOrGet(InsnOp.BALOAD)) }
fun KBMethod.caload()         { addOperation(KBSingleOP.createOrGet(InsnOp.CALOAD)) }
fun KBMethod.saload()         { addOperation(KBSingleOP.createOrGet(InsnOp.SALOAD)) }

fun KBMethod.iastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.IASTORE)) }
fun KBMethod.lastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.LASTORE)) }
fun KBMethod.fastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.FASTORE)) }
fun KBMethod.dastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.DASTORE)) }
fun KBMethod.aastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.AASTORE)) }
fun KBMethod.bastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.BASTORE)) }
fun KBMethod.castore()        { addOperation(KBSingleOP.createOrGet(InsnOp.CASTORE)) }
fun KBMethod.sastore()        { addOperation(KBSingleOP.createOrGet(InsnOp.SASTORE)) }

fun KBMethod.pop()            { addOperation(KBSingleOP.createOrGet(InsnOp.POP)) }
fun KBMethod.pop2()           { addOperation(KBSingleOP.createOrGet(InsnOp.POP2)) }
fun KBMethod.dup()            { addOperation(KBSingleOP.createOrGet(InsnOp.DUP)) }
fun KBMethod.dup_x1()         { addOperation(KBSingleOP.createOrGet(InsnOp.DUP_X1)) }
fun KBMethod.dup_x2()         { addOperation(KBSingleOP.createOrGet(InsnOp.DUP_X2)) }
fun KBMethod.dup2()           { addOperation(KBSingleOP.createOrGet(InsnOp.DUP2)) }
fun KBMethod.dup2_x1()        { addOperation(KBSingleOP.createOrGet(InsnOp.DUP2_X1)) }
fun KBMethod.dup2_x2()        { addOperation(KBSingleOP.createOrGet(InsnOp.DUP2_X2)) }
fun KBMethod.swap()           { addOperation(KBSingleOP.createOrGet(InsnOp.SWAP)) }

fun KBMethod.iadd()           { addOperation(KBSingleOP.createOrGet(InsnOp.IADD)) }
fun KBMethod.ladd()           { addOperation(KBSingleOP.createOrGet(InsnOp.LADD)) }
fun KBMethod.fadd()           { addOperation(KBSingleOP.createOrGet(InsnOp.FADD)) }
fun KBMethod.dadd()           { addOperation(KBSingleOP.createOrGet(InsnOp.DADD)) }
fun KBMethod.isub()           { addOperation(KBSingleOP.createOrGet(InsnOp.ISUB)) }
fun KBMethod.lsub()           { addOperation(KBSingleOP.createOrGet(InsnOp.LSUB)) }
fun KBMethod.fsub()           { addOperation(KBSingleOP.createOrGet(InsnOp.FSUB)) }
fun KBMethod.dsub()           { addOperation(KBSingleOP.createOrGet(InsnOp.DSUB)) }
fun KBMethod.imul()           { addOperation(KBSingleOP.createOrGet(InsnOp.IMUL)) }
fun KBMethod.lmul()           { addOperation(KBSingleOP.createOrGet(InsnOp.LMUL)) }
fun KBMethod.fmul()           { addOperation(KBSingleOP.createOrGet(InsnOp.FMUL)) }
fun KBMethod.dmul()           { addOperation(KBSingleOP.createOrGet(InsnOp.DMUL)) }
fun KBMethod.idiv()           { addOperation(KBSingleOP.createOrGet(InsnOp.IDIV)) }
fun KBMethod.ldiv()           { addOperation(KBSingleOP.createOrGet(InsnOp.LDIV)) }
fun KBMethod.fdiv()           { addOperation(KBSingleOP.createOrGet(InsnOp.FDIV)) }
fun KBMethod.ddiv()           { addOperation(KBSingleOP.createOrGet(InsnOp.DDIV)) }
fun KBMethod.irem()           { addOperation(KBSingleOP.createOrGet(InsnOp.IREM)) }
fun KBMethod.lrem()           { addOperation(KBSingleOP.createOrGet(InsnOp.LREM)) }
fun KBMethod.frem()           { addOperation(KBSingleOP.createOrGet(InsnOp.FREM)) }
fun KBMethod.drem()           { addOperation(KBSingleOP.createOrGet(InsnOp.DREM)) }

fun KBMethod.ineg()           { addOperation(KBSingleOP.createOrGet(InsnOp.INEG)) }
fun KBMethod.lneg()           { addOperation(KBSingleOP.createOrGet(InsnOp.LNEG)) }
fun KBMethod.fneg()           { addOperation(KBSingleOP.createOrGet(InsnOp.FNEG)) }
fun KBMethod.dneg()           { addOperation(KBSingleOP.createOrGet(InsnOp.DNEG)) }

fun KBMethod.ishl()           { addOperation(KBSingleOP.createOrGet(InsnOp.ISHL)) }
fun KBMethod.lshl()           { addOperation(KBSingleOP.createOrGet(InsnOp.LSHL)) }
fun KBMethod.ishr()           { addOperation(KBSingleOP.createOrGet(InsnOp.ISHR)) }
fun KBMethod.lshr()           { addOperation(KBSingleOP.createOrGet(InsnOp.LSHR)) }
fun KBMethod.iushr()          { addOperation(KBSingleOP.createOrGet(InsnOp.IUSHR)) }
fun KBMethod.lushr()          { addOperation(KBSingleOP.createOrGet(InsnOp.LUSHR)) }

fun KBMethod.iand()           { addOperation(KBSingleOP.createOrGet(InsnOp.IAND)) }
fun KBMethod.land()           { addOperation(KBSingleOP.createOrGet(InsnOp.LAND)) }
fun KBMethod.ior()            { addOperation(KBSingleOP.createOrGet(InsnOp.IOR)) }
fun KBMethod.lor()            { addOperation(KBSingleOP.createOrGet(InsnOp.LOR)) }
fun KBMethod.ixor()           { addOperation(KBSingleOP.createOrGet(InsnOp.IXOR)) }
fun KBMethod.lxor()           { addOperation(KBSingleOP.createOrGet(InsnOp.LXOR)) }

fun KBMethod.i2l()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2L)) }
fun KBMethod.i2f()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2F)) }
fun KBMethod.i2d()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2D)) }
fun KBMethod.l2i()            { addOperation(KBSingleOP.createOrGet(InsnOp.L2I)) }
fun KBMethod.l2f()            { addOperation(KBSingleOP.createOrGet(InsnOp.L2F)) }
fun KBMethod.l2d()            { addOperation(KBSingleOP.createOrGet(InsnOp.L2D)) }
fun KBMethod.f2i()            { addOperation(KBSingleOP.createOrGet(InsnOp.F2I)) }
fun KBMethod.f2l()            { addOperation(KBSingleOP.createOrGet(InsnOp.F2L)) }
fun KBMethod.f2d()            { addOperation(KBSingleOP.createOrGet(InsnOp.F2D)) }
fun KBMethod.d2i()            { addOperation(KBSingleOP.createOrGet(InsnOp.D2I)) }
fun KBMethod.d2l()            { addOperation(KBSingleOP.createOrGet(InsnOp.D2L)) }
fun KBMethod.d2f()            { addOperation(KBSingleOP.createOrGet(InsnOp.D2F)) }

fun KBMethod.i2b()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2B)) }
fun KBMethod.i2c()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2C)) }
fun KBMethod.i2s()            { addOperation(KBSingleOP.createOrGet(InsnOp.I2S)) }

fun KBMethod.lcmp()           { addOperation(KBSingleOP.createOrGet(InsnOp.LCMP)) }
fun KBMethod.fcmpl()          { addOperation(KBSingleOP.createOrGet(InsnOp.FCMPL)) }
fun KBMethod.fcmpg()          { addOperation(KBSingleOP.createOrGet(InsnOp.FCMPG)) }
fun KBMethod.dcmpl()          { addOperation(KBSingleOP.createOrGet(InsnOp.DCMPL)) }
fun KBMethod.dcmpg()          { addOperation(KBSingleOP.createOrGet(InsnOp.DCMPG)) }

fun KBMethod.ireturn()        { addOperation(KBSingleOP.createOrGet(InsnOp.IRETURN)) }
fun KBMethod.lreturn()        { addOperation(KBSingleOP.createOrGet(InsnOp.LRETURN)) }
fun KBMethod.freturn()        { addOperation(KBSingleOP.createOrGet(InsnOp.FRETURN)) }
fun KBMethod.dreturn()        { addOperation(KBSingleOP.createOrGet(InsnOp.DRETURN)) }
fun KBMethod.areturn()        { addOperation(KBSingleOP.createOrGet(InsnOp.ARETURN)) }
fun KBMethod.return_()        { addOperation(KBSingleOP.createOrGet(InsnOp.RETURN)) } // “return” is a keyword

fun KBMethod.areturn_null()   { aconst_null(); areturn() }

fun KBMethod.arraylength()    { addOperation(KBSingleOP.createOrGet(InsnOp.ARRAYLENGTH)) }
fun KBMethod.athrow()         { addOperation(KBSingleOP.createOrGet(InsnOp.ATHROW)) }

fun KBMethod.monitorenter()   { addOperation(KBSingleOP.createOrGet(InsnOp.MONITORENTER)) }
fun KBMethod.monitorexit()    { addOperation(KBSingleOP.createOrGet(InsnOp.MONITOREXIT)) }
