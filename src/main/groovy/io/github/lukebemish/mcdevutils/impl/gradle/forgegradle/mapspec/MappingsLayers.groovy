package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec

class MappingsLayers {
    final List<MapSpec> layers = new LinkedList<>()
    InitialMapSpec base;

    MappingsLayers addLayer(MapSpec spec) {
        layers.add(spec)
        return this
    }

    MappingsLayers addInitialLayer(InitialMapSpec spec) {
        base = spec
        return this
    }

    MappingsLayers parchment(String version) {
        return addInitialLayer(new ParchmentMapSpec(version))
    }

    MappingsLayers official(String version) {
        return addInitialLayer(new OfficialMapSpec(version))
    }
}
