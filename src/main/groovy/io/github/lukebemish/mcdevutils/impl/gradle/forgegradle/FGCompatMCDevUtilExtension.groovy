package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle

import io.github.lukebemish.mcdevutils.impl.gradle.MCDevUtilExtension
import io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec.MappingsLayers

abstract class FGCompatMCDevUtilExtension extends MCDevUtilExtension {
    Closure<MappingsLayers> mappings

    void setMappings(Closure<MappingsLayers> action) {
        this.mappings = action
        this.mappings.delegate = new MappingsLayers();
    }

    FGCompatMCDevUtilExtension() {
        super()
        this.mappings = { null }
    }
}
