package io.github.lukebemish.mcdevutils.impl.gradle.forgegradle.mapspec

import net.minecraftforge.gradle.mcp.MCPRepo
import org.gradle.api.Project

interface InitialMapSpec {
    File getMappingsFile(MCPRepo mcpRepo, Project project) throws IOException;
}