package org.bezsahara.minikotlin.builder.opcodes.codes

import org.bezsahara.minikotlin.builder.opcodes.codes.SWord.*

class InsnOp private constructor(
    @JvmField val opcode: Int,
    @JvmField val stackTaken: Array<SWord>,
    @JvmField val stackGiven: Array<SWord>,
    @JvmField val name: String
) {

    companion object {

        /* ──────────── 0–15  :  constants ──────────── */

        @JvmField val NOP         = InsnOp(0,  emptyArrayOfSWord,              emptyArrayOfSWord, "NOP")
        // does nothing – no stack effect

        @JvmField val ACONST_NULL = InsnOp(1,  emptyArrayOfSWord,              arrayOf(A), "ACONST_NULL")
        // push a null reference

        @JvmField val ICONST_M1   = InsnOp(2,  emptyArrayOfSWord,              arrayOf(I), "ICONST_M1")  // push int −1
        @JvmField val ICONST_0    = InsnOp(3,  emptyArrayOfSWord,              arrayOf(I), "ICONST_0")
        @JvmField val ICONST_1    = InsnOp(4,  emptyArrayOfSWord,              arrayOf(I), "ICONST_1")
        @JvmField val ICONST_2    = InsnOp(5,  emptyArrayOfSWord,              arrayOf(I), "ICONST_2")
        @JvmField val ICONST_3    = InsnOp(6,  emptyArrayOfSWord,              arrayOf(I), "ICONST_3")
        @JvmField val ICONST_4    = InsnOp(7,  emptyArrayOfSWord,              arrayOf(I), "ICONST_4")
        @JvmField val ICONST_5    = InsnOp(8,  emptyArrayOfSWord,              arrayOf(I), "ICONST_5")

        @JvmField val LCONST_0    = InsnOp(9,  emptyArrayOfSWord,              arrayOf(L), "LCONST_0")  // push long 0
        @JvmField val LCONST_1    = InsnOp(10, emptyArrayOfSWord,              arrayOf(L), "LCONST_1")

        @JvmField val FCONST_0    = InsnOp(11, emptyArrayOfSWord,              arrayOf(F), "FCONST_0")  // float 0.0 (cat-1)
        @JvmField val FCONST_1    = InsnOp(12, emptyArrayOfSWord,              arrayOf(F), "FCONST_1")
        @JvmField val FCONST_2    = InsnOp(13, emptyArrayOfSWord,              arrayOf(F), "FCONST_2")

        @JvmField val DCONST_0    = InsnOp(14, emptyArrayOfSWord,              arrayOf(D), "DCONST_0")  // double 0.0 (cat-2)
        @JvmField val DCONST_1    = InsnOp(15, emptyArrayOfSWord,              arrayOf(D), "DCONST_1")

        /* ──────────── 46–53  :  array loads ──────────── */

        @JvmField val IALOAD      = InsnOp(46, arrayOf(Arr.I, I),                  arrayOf(I), "IALOAD") // ref, idx → int
        @JvmField val LALOAD      = InsnOp(47, arrayOf(Arr.L, I),                  arrayOf(L), "LALOAD") // ref, idx → long
        @JvmField val FALOAD      = InsnOp(48, arrayOf(Arr.F, I),                  arrayOf(F), "FALOAD") // ref, idx → float(cat-1)
        @JvmField val DALOAD      = InsnOp(49, arrayOf(Arr.D, I),                  arrayOf(D), "DALOAD") // ref, idx → double
        @JvmField val AALOAD      = InsnOp(50, arrayOf(Arr.A, I),                  arrayOf(A), "AALOAD") // ref, idx → ref
        @JvmField val BALOAD      = InsnOp(51, arrayOf(Arr.B, I),                  arrayOf(I), "BALOAD") // ref, idx → byte(cat-1)
        @JvmField val CALOAD      = InsnOp(52, arrayOf(Arr.C, I),                  arrayOf(I), "CALOAD") // ref, idx → char(cat-1)
        @JvmField val SALOAD      = InsnOp(53, arrayOf(Arr.S, I),                  arrayOf(I), "SALOAD") // ref, idx → short(cat-1)

        /* ──────────── 79–86  :  array stores ──────────── */

        @JvmField val IASTORE     = InsnOp(79, arrayOf(Arr.I, I, I),               emptyArrayOfSWord, "IASTORE") // store int into int[]
        @JvmField val LASTORE     = InsnOp(80, arrayOf(Arr.L, I, L),               emptyArrayOfSWord, "LASTORE") // store long into long[]
        @JvmField val FASTORE     = InsnOp(81, arrayOf(Arr.F, I, F),               emptyArrayOfSWord, "FASTORE") // store float into float[]
        @JvmField val DASTORE     = InsnOp(82, arrayOf(Arr.D, I, D),               emptyArrayOfSWord, "DASTORE") // store double into double[]
        @JvmField val AASTORE     = InsnOp(83, arrayOf(Arr.A, I, A),               emptyArrayOfSWord, "AASTORE") // store reference into Object[]
        @JvmField val BASTORE     = InsnOp(84, arrayOf(Arr.B, I, I),               emptyArrayOfSWord, "BASTORE") // store byte or boolean into byte[] or boolean[]
        @JvmField val CASTORE     = InsnOp(85, arrayOf(Arr.C, I, I),               emptyArrayOfSWord, "CASTORE") // store char into char[]
        @JvmField val SASTORE     = InsnOp(86, arrayOf(Arr.S, I, I),               emptyArrayOfSWord, "SASTORE") // store short into short[]

        /* ──────────── 87–95  :  simple stack ops ──────────── */
        // special cases in verifier
        @JvmField val POP         = InsnOp(87, arrayOf(W32),                   emptyArrayOfSWord, "POP")          // pop 1 slot
        @JvmField val POP2        = InsnOp(88, arrayOf(W64Both),               emptyArrayOfSWord, "POP2")          // pop 2 slots (1 ×64 or 2 ×32)

        @JvmField val DUP         = InsnOp(89, arrayOf(W32),                   arrayOf(W32, W32), "DUP")      // X → X X

        // Insert top value just beneath the next one (or two) values — considering their width. xN <- N is how much 32 bit slots beaneath.
        @JvmField val DUP_X1      = InsnOp(90, arrayOf(W32, W32),              arrayOf(W32, W32, W32), "DUP_X1")   // X2 X1 → X1 X2 X1
        @JvmField val DUP_X2      = InsnOp(91, arrayOf(W64Both, W32),          arrayOf(W32, W64Both, W32), "DUP_X2") // (see spec)


        @JvmField val DUP2        = InsnOp(92, arrayOf(W64Both),               arrayOf(W64Both, W64Both), "DUP2") // duplicate 2 slots
        @JvmField val DUP2_X1     = InsnOp(93, arrayOf(I, W64Both),            arrayOf(W64Both, I, W64Both), "DUP2_X1")
        @JvmField val DUP2_X2     = InsnOp(94, arrayOf(W64Both, W64Both),      arrayOf(W64Both, W64Both, W64Both), "DUP2_X2")

        @JvmField val SWAP        = InsnOp(95, arrayOf(W32, W32),              arrayOf(W32, W32), "SWAP")      // X2 X1 → X1 X2

        /* ──────────── 96–131 :  arithmetic / logic ──────────── */

        @JvmField val IADD        = InsnOp(96,  arrayOf(I, I),                 arrayOf(I), "IADD")
        @JvmField val LADD        = InsnOp(97,  arrayOf(L, L),                 arrayOf(L), "LADD")
        @JvmField val FADD        = InsnOp(98,  arrayOf(F, F),                 arrayOf(F), "FADD") // float
        @JvmField val DADD        = InsnOp(99,  arrayOf(D, D),                 arrayOf(D), "DADD")

        @JvmField val ISUB        = InsnOp(100, arrayOf(I, I),                 arrayOf(I), "ISUB")
        @JvmField val LSUB        = InsnOp(101, arrayOf(L, L),                 arrayOf(L), "LSUB")
        @JvmField val FSUB        = InsnOp(102, arrayOf(F, F),                 arrayOf(F), "FSUB")
        @JvmField val DSUB        = InsnOp(103, arrayOf(D, D),                 arrayOf(D), "DSUB")

        @JvmField val IMUL        = InsnOp(104, arrayOf(I, I),                 arrayOf(I), "IMUL")
        @JvmField val LMUL        = InsnOp(105, arrayOf(L, L),                 arrayOf(L), "LMUL")
        @JvmField val FMUL        = InsnOp(106, arrayOf(F, F),                 arrayOf(F), "FMUL")
        @JvmField val DMUL        = InsnOp(107, arrayOf(D, D),                 arrayOf(D), "DMUL")

        @JvmField val IDIV        = InsnOp(108, arrayOf(I, I),                 arrayOf(I), "IDIV")
        @JvmField val LDIV        = InsnOp(109, arrayOf(L, L),                 arrayOf(L), "LDIV")
        @JvmField val FDIV        = InsnOp(110, arrayOf(F, F),                 arrayOf(F), "FDIV")
        @JvmField val DDIV        = InsnOp(111, arrayOf(D, D),                 arrayOf(D), "DDIV")

        @JvmField val IREM        = InsnOp(112, arrayOf(I, I),                 arrayOf(I), "IREM")
        @JvmField val LREM        = InsnOp(113, arrayOf(L, L),                 arrayOf(L), "LREM")
        @JvmField val FREM        = InsnOp(114, arrayOf(F, F),                 arrayOf(F), "FREM")
        @JvmField val DREM        = InsnOp(115, arrayOf(D, D),                 arrayOf(D), "DREM")

        /* unary −X */ // TODO impl these in mk

        @JvmField val INEG        = InsnOp(116, arrayOf(I),                    arrayOf(I), "INEG")
        @JvmField val LNEG        = InsnOp(117, arrayOf(L),                    arrayOf(L), "LNEG")
        @JvmField val FNEG        = InsnOp(118, arrayOf(F),                    arrayOf(F), "FNEG")
        @JvmField val DNEG        = InsnOp(119, arrayOf(D),                    arrayOf(D), "DNEG")

        /* shifts  (shift-count is int on top) */

        @JvmField val ISHL        = InsnOp(120, arrayOf(I, I),                 arrayOf(I), "ISHL") // cnt,int → int
        @JvmField val LSHL        = InsnOp(121, arrayOf(I, L),                 arrayOf(L), "LSHL") // cnt,long → long
        @JvmField val ISHR        = InsnOp(122, arrayOf(I, I),                 arrayOf(I), "ISHR")
        @JvmField val LSHR        = InsnOp(123, arrayOf(I, L),                 arrayOf(L), "LSHR")
        @JvmField val IUSHR       = InsnOp(124, arrayOf(I, I),                 arrayOf(I), "IUSHR")
        @JvmField val LUSHR       = InsnOp(125, arrayOf(I, L),                 arrayOf(L), "LUSHR")

        /* bitwise */

        @JvmField val IAND        = InsnOp(126, arrayOf(I, I),                 arrayOf(I), "IAND")
        @JvmField val LAND        = InsnOp(127, arrayOf(L, L),                 arrayOf(L), "LAND")
        @JvmField val IOR         = InsnOp(128, arrayOf(I, I),                 arrayOf(I), "IOR")
        @JvmField val LOR         = InsnOp(129, arrayOf(L, L),                 arrayOf(L), "LOR")
        @JvmField val IXOR        = InsnOp(130, arrayOf(I, I),                 arrayOf(I), "IXOR")
        @JvmField val LXOR        = InsnOp(131, arrayOf(L, L),                 arrayOf(L), "LXOR")

        /* ──────────── 133–147 :  numeric casts ──────────── */

        @JvmField val I2L         = InsnOp(133, arrayOf(I),                    arrayOf(L), "I2L")
        @JvmField val I2F         = InsnOp(134, arrayOf(I),                    arrayOf(F), "I2F")  // → float(cat-1)
        @JvmField val I2D         = InsnOp(135, arrayOf(I),                    arrayOf(D), "I2D")
        @JvmField val L2I         = InsnOp(136, arrayOf(L),                    arrayOf(I), "L2I")
        @JvmField val L2F         = InsnOp(137, arrayOf(L),                    arrayOf(F), "L2F")
        @JvmField val L2D         = InsnOp(138, arrayOf(L),                    arrayOf(D), "L2D")
        @JvmField val F2I         = InsnOp(139, arrayOf(F),                    arrayOf(I), "F2I")
        @JvmField val F2L         = InsnOp(140, arrayOf(F),                    arrayOf(L), "F2L")
        @JvmField val F2D         = InsnOp(141, arrayOf(F),                    arrayOf(D), "F2D")
        @JvmField val D2I         = InsnOp(142, arrayOf(D),                    arrayOf(I), "D2I")
        @JvmField val D2L         = InsnOp(143, arrayOf(D),                    arrayOf(L), "D2L")
        @JvmField val D2F         = InsnOp(144, arrayOf(D),                    arrayOf(F), "D2F")

        /* int-to-smaller */

        @JvmField val I2B         = InsnOp(145, arrayOf(I),                    arrayOf(I), "I2B")  // → byte (still 32-bit)
        @JvmField val I2C         = InsnOp(146, arrayOf(I),                    arrayOf(I), "I2C")  // → char
        @JvmField val I2S         = InsnOp(147, arrayOf(I),                    arrayOf(I), "I2S")  // → short

        /* ──────────── 148–152 :  comparisons ──────────── */

        @JvmField val LCMP        = InsnOp(148, arrayOf(L, L),                 arrayOf(I), "LCMP")  // long,long → int
        @JvmField val FCMPL       = InsnOp(149, arrayOf(F, F),                 arrayOf(I), "FCMPL")  // float,float → int
        @JvmField val FCMPG       = InsnOp(150, arrayOf(F, F),                 arrayOf(I), "FCMPG")
        @JvmField val DCMPL       = InsnOp(151, arrayOf(D, D),                 arrayOf(I), "DCMPL")  // double,double → int
        @JvmField val DCMPG       = InsnOp(152, arrayOf(D, D),                 arrayOf(I), "DCMPG")

        /* ──────────── 172–177 :  returns ──────────── */

        @JvmField val IRETURN     = InsnOp(172, arrayOf(I),                    emptyArrayOfSWord, "IRETURN")   // pop int → caller
        @JvmField val LRETURN     = InsnOp(173, arrayOf(L),                    emptyArrayOfSWord, "LRETURN")
        @JvmField val FRETURN     = InsnOp(174, arrayOf(F),                    emptyArrayOfSWord, "FRETURN")
        @JvmField val DRETURN     = InsnOp(175, arrayOf(D),                    emptyArrayOfSWord, "DRETURN")
        @JvmField val ARETURN     = InsnOp(176, arrayOf(A),                    emptyArrayOfSWord, "ARETURN")
        @JvmField val RETURN      = InsnOp(177, emptyArrayOfSWord,             emptyArrayOfSWord, "RETURN")   // void

        /* ──────────── misc ──────────── */

        @JvmField val ARRAYLENGTH = InsnOp(190, arrayOf(Arr.All),                    arrayOf(I), "ARRAYLENGTH")  // ref → int
        @JvmField val ATHROW      = InsnOp(191, arrayOf(ACommon.TH),                    emptyArrayOfSWord, "ATHROW")   // pop throwable, never returns

        @JvmField val MONITORENTER= InsnOp(194, arrayOf(A),                    emptyArrayOfSWord, "MONITORENTER")   // pop ref (monitor enter)
        @JvmField val MONITOREXIT = InsnOp(195, arrayOf(A),                    emptyArrayOfSWord, "MONITOREXIT")   // pop ref (monitor exit)

        @JvmField
        val ALL_INSNS: Array<InsnOp> = arrayOf(
            NOP,
            ACONST_NULL,
            ICONST_M1, ICONST_0, ICONST_1, ICONST_2, ICONST_3, ICONST_4, ICONST_5,
            LCONST_0, LCONST_1,
            FCONST_0, FCONST_1, FCONST_2,
            DCONST_0, DCONST_1,
            IALOAD, LALOAD, FALOAD, DALOAD, AALOAD, BALOAD, CALOAD, SALOAD,
            IASTORE, LASTORE, FASTORE, DASTORE, AASTORE, BASTORE, CASTORE, SASTORE,
            POP, POP2,
            DUP, DUP_X1, DUP_X2, DUP2, DUP2_X1, DUP2_X2, SWAP,
            IADD, LADD, FADD, DADD,
            ISUB, LSUB, FSUB, DSUB,
            IMUL, LMUL, FMUL, DMUL,
            IDIV, LDIV, FDIV, DDIV,
            IREM, LREM, FREM, DREM,
            INEG, LNEG, FNEG, DNEG,
            ISHL, LSHL, ISHR, LSHR, IUSHR, LUSHR,
            IAND, LAND, IOR, LOR, IXOR, LXOR,
            I2L, I2F, I2D, L2I, L2F, L2D, F2I, F2L, F2D, D2I, D2L, D2F,
            I2B, I2C, I2S,
            LCMP, FCMPL, FCMPG, DCMPL, DCMPG,
            IRETURN, LRETURN, FRETURN, DRETURN, ARETURN, RETURN,
            ARRAYLENGTH, ATHROW,
            MONITORENTER, MONITOREXIT
        )
    }
}
