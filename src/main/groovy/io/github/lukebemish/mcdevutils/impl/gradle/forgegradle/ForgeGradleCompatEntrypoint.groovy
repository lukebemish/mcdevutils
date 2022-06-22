package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle


import net.minecraftforge.gradle.mcp.ChannelProvidersExtension
import org.gradle.api.Project

class ForgeGradleCompatEntrypoint {
    static void enter(Project project, FGCompatMCDevUtilExtension extension) {
        ChannelProvidersExtension channelProviders = project.getExtensions().findByType(ChannelProvidersExtension.class)
        if (channelProviders == null)
            throw new IllegalStateException("For ForgeGradle compatibility, the MCDevUtils plugin must be applied after the ForgeGradle one. ")

        TinyRemapperChannelProvider channelProvider = new TinyRemapperChannelProvider(extension)
        channelProviders.addProvider(channelProvider)
    }
}
