package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle

import com.google.common.collect.ImmutableSet
import io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec.MappingsLayers
import net.minecraftforge.gradle.mcp.ChannelProvider
import net.minecraftforge.gradle.mcp.MCPRepo
import org.gradle.api.Project

class TinyRemapperChannelProvider implements ChannelProvider {

    final FGCompatMCDevUtilExtension extension

    TinyRemapperChannelProvider(FGCompatMCDevUtilExtension extension) {
        this.extension = extension
    }

    @Override
    Set<String> getChannels() {
        return ImmutableSet.of("mcdevutils")
    }

    @Override
    File getMappingsFile(MCPRepo mcpRepo, Project project, String channel, String version) throws IOException {
        MappingsLayers layers = extension.mappings.call()
        File base = layers.base.getMappingsFile(mcpRepo, project)

        return base
    }
}
