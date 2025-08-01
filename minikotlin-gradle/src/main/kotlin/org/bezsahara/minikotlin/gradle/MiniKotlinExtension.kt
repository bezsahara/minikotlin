package org.bezsahara.minikotlin.gradle

import org.gradle.api.file.DirectoryProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.provider.Property
import javax.inject.Inject


enum class MiniKotlinRunMode {
    MANUAL,   // user wires tasks manually
    AUTO,      // plugin wires generateMiniKotlin after classes
}

abstract class MiniKotlinExtension @Inject constructor(objects: ObjectFactory) {
    val runMode: Property<MiniKotlinRunMode> =
        objects.property(MiniKotlinRunMode::class.java).convention(MiniKotlinRunMode.AUTO)

    // stays inside build/
    val generateFolder: DirectoryProperty =
        objects.directoryProperty()
            .convention(objects.directoryProperty()
                .fileValue(java.io.File("build/minikotlin-gen")))

    // "<projectDir>/minikotlin-stubs"
    val stubFolder: DirectoryProperty =
        objects.directoryProperty()
            .convention(objects.directoryProperty()
                .fileValue(java.io.File("minikotlin-stubs")))
}