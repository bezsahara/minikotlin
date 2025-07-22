package org.bezsahara.minikotlin.builder.experiment

//sealed interface PropertyRule<Vis: PRVis, Mod: PropertyRule>

//typealias PRMod_NoAbstract = PropertyRule<
//        PRModVal,
//        PRModVal,
//        PRModVal.None, // static
//        PRModVal.None, // abstract
//        PRModVal.None, // native
//        PRModVal,
//        PRModVal
//        >

sealed interface PRVis {
    interface None : PRVis
}

sealed interface PRModVal {
    interface None: PRModVal
}


interface NPropertyRule

interface PropertyRule<
        out Final : PRModVal,
        out Synchronised : PRModVal,
        out Static : PRModVal,
        out Abstract : PRModVal,
        out Native : PRModVal,
        out Strictfp : PRModVal,
        out Default : PRModVal,
        out Private: PRVis,
        out Public: PRVis,
        out Protected: PRVis,
        > : NPropertyRule
//Vis

typealias PRMod_None_PublicSet = PropertyRule<
        PRModVal.None,    // final
        PRModVal.None,    // synchronized
        PRModVal.None,    // static
        PRModVal.None,    // abstract
        PRModVal.None,    // native
        PRModVal.None,    // strictfp
        PRModVal.None,    // default
        PRVis.None,       // private not set
        PRVis,    // public set
        PRVis.None        // protected not set
        >

typealias PRMod_None_PrivateSet = PropertyRule<
        PRModVal.None,    // final
        PRModVal.None,    // synchronized
        PRModVal.None,    // static
        PRModVal.None,    // abstract
        PRModVal.None,    // native
        PRModVal.None,    // strictfp
        PRModVal.None,    // default
        PRVis,    // private set
        PRVis.None,       // public not set
        PRVis.None        // protected not set
        >

typealias PRMod_None_ProtectedSet = PropertyRule<
        PRModVal.None,    // final
        PRModVal.None,    // synchronized
        PRModVal.None,    // static
        PRModVal.None,    // abstract
        PRModVal.None,    // native
        PRModVal.None,    // strictfp
        PRModVal.None,    // default
        PRVis.None,       // private not set
        PRVis.None,       // public not set
        PRVis     // protected set
        >


//

typealias PRMod_None = PropertyRule<
        PRModVal.None,    // final
        PRModVal.None,    // synchronized
        PRModVal.None,    // static
        PRModVal.None,    // abstract
        PRModVal.None,    // native
        PRModVal.None,    // strictfp
        PRModVal.None,    // default
        PRVis,            // private not yet set
        PRVis,            // public not yet set
        PRVis             // protected not yet set
        >

typealias PRMod_NoAbstract = PropertyRule<
        PRModVal.None,    // final (incompatible with abstract)
        PRModVal.None,    // synchronized (requires method body)
        PRModVal.None,    // static (conflicts with abstract semantics)
        PRModVal.None,    // abstract (excluded)
        PRModVal.None,    // native (conflicts with abstract)
        PRModVal,         // strictfp (allowed but has no effect)
        PRModVal.None,    // default (interface default requires body)
        PRVis.None,       // private (abstract cannot be private)
        PRVis,            // public allowed
        PRVis             // protected allowed
        >

typealias PRMod_NoFinal = PropertyRule<
        PRModVal.None,    // final (excluded)
        PRModVal,         // synchronized
        PRModVal,         // static
        PRModVal.None,    // abstract (conflicts with final)
        PRModVal,         // native
        PRModVal,         // strictfp
        PRModVal.None,    // default (conflicts with final)
        PRVis,            // private
        PRVis,            // public
        PRVis             // protected
        >

typealias PRMod_NoSynchronised<A,B,C,D,E> = PropertyRule<
        A,         // final
        PRModVal.None,    // synchronized (excluded)
        B,         // static
        PRModVal.None,    // abstract (abstract cannot be synchronized)
        C,         // native
        D,         // strictfp
        E,         // default
        PRVis,            // private
        PRVis,            // public
        PRVis             // protected
        >

typealias PRMod_NoStatic = PropertyRule<
        PRModVal,         // final
        PRModVal,         // synchronized
        PRModVal.None,    // static (excluded)
        PRModVal.None,    // abstract (conflicts with static in methods)
        PRModVal,         // native
        PRModVal,         // strictfp
        PRModVal.None,    // default (conflicts with static)
        PRVis,            // private
        PRVis,            // public
        PRVis             // protected
        >

typealias PRMod_NoNative = PropertyRule<
        PRModVal,         // final
        PRModVal,         // synchronized
        PRModVal,         // static
        PRModVal.None,    // abstract (conflicts with native)
        PRModVal.None,    // native (excluded)
        PRModVal,         // strictfp
        PRModVal.None,    // default (conflicts with native)
        PRVis,            // private
        PRVis,            // public
        PRVis             // protected
        >

typealias PRMod_NoStrictfp = PropertyRule<
        PRModVal,         // final
        PRModVal,         // synchronized
        PRModVal,         // static
        PRModVal,         // abstract
        PRModVal,         // native
        PRModVal.None,    // strictfp (excluded)
        PRModVal,         // default
        PRVis,            // private
        PRVis,            // public
        PRVis             // protected
        >

typealias PRMod_NoDefault = PropertyRule<
        PRModVal.None,    // final (conflicts with default)
        PRModVal,         // synchronized
        PRModVal.None,    // static (default must be instance method)
        PRModVal.None,    // abstract (default must have body)
        PRModVal.None,    // native (conflicts with default)
        PRModVal,         // strictfp
        PRModVal.None,    // default (excluded)
        PRVis,            // private
        PRVis.None,        // public (default is public by default)
        PRVis             // protected
        >

//val defaultNone = object : PropertyRule<PRModVal.None>