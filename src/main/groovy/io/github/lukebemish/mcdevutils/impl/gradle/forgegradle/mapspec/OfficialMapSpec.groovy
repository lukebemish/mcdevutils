package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec

import net.minecraftforge.gradle.mcp.ChannelProvider
import net.minecraftforge.gradle.mcp.ChannelProvidersExtension
import net.minecraftforge.gradle.mcp.MCPRepo
import org.gradle.api.Project

class OfficialMapSpec implements InitialMapSpec {

    final String version;

    OfficialMapSpec(String version) {
        this.version = version;
    }

    @Override
    File getMappingsFile(MCPRepo mcpRepo, Project project) throws IOException {
        ChannelProvidersExtension channelProviders = project.getExtensions().findByType(ChannelProvidersExtension.class)
        if (channelProviders != null) {
            ChannelProvider official = channelProviders.getProvider("official")
            if (official != null) {
                return official.getMappingsFile(mcpRepo, project, "official", version);
            }
        }
        throw new IllegalStateException("For ForgeGradle compatibility, the MCDevUtils plugin must be applied after the ForgeGradle one. ")
    }
}
