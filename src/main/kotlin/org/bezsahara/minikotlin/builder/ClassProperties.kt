package org.bezsahara.minikotlin.builder

// Note: `verifier`, `debug`, and `trackVariables` each add extra processing time to class generation.
/**
 * Configuration for controlling various class generation options.
 *
 * @property verifier Enables the bytecode verifier. Adds type and control-flow checks during class generation.
 * @property debug Enables debug information in the bytecode, such as source line mappings.
 *                Useful for identifying which method caused verification errors.
 * @property trackVariables Inserts local variable name metadata into the generated `.class` file.
 *                          Adds extra generation time if enabled.
 * @property syntheticPrefix Prefix used for naming synthetic fields and other internally generated elements.
 */
data class ClassProperties(
    val verifier: Boolean,
    val debug: Boolean,
    val trackVariables: Boolean,
    val syntheticPrefix: String
) {
    /** Derived key name based on the [syntheticPrefix]. */
    val syntheticKey: String = "${syntheticPrefix}key"

    companion object {
        /** Default configuration: all features enabled, using "synth0" prefix. */
        val Default = ClassProperties(verifier = true, debug = true, trackVariables = true, syntheticPrefix = "synth0")

        /** Minimal configuration: all features disabled, using "synth0" prefix. */
        val Disabled = ClassProperties(verifier = false, debug = false, trackVariables = false, syntheticPrefix = "synth0")
    }
}