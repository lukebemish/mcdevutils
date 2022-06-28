package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec

import net.minecraftforge.gradle.mcp.MCPRepo
import org.gradle.api.Project

class IntermediaryMapSpec implements InitialMapSpec {

    final String version

    IntermediaryMapSpec(String version) {
        this.version = version
    }

    @Override
    File getMappingsFile(MCPRepo mcpRepo, Project project) throws IOException {
        
        return null
    }
}
