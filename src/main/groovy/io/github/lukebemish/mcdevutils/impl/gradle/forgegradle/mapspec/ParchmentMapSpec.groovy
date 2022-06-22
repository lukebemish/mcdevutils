package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec

import net.minecraftforge.gradle.mcp.ChannelProvider
import net.minecraftforge.gradle.mcp.ChannelProvidersExtension
import net.minecraftforge.gradle.mcp.MCPRepo
import org.gradle.api.Project

class ParchmentMapSpec implements InitialMapSpec {

    final String version;

    ParchmentMapSpec(String version) {
        this.version = version;
    }

    @Override
    File getMappingsFile(MCPRepo mcpRepo, Project project) throws IOException {
        ChannelProvidersExtension channelProviders = project.getExtensions().findByType(ChannelProvidersExtension.class)
        if (channelProviders != null) {
            ChannelProvider official = channelProviders.getProvider("parchment")
            if (official != null) {
                return official.getMappingsFile(mcpRepo, project, "parchment", version);
            }
        }
        throw new IllegalStateException("For Librarian compatibility, the MCDevUtils plugin must be applied after the Librarian one. ")
    }
}
