package io.github.lukebemish.mcdevutils.impl.gradle

import io.github.lukebemish.mcdevutils.impl.gradle.visiting.NamingClassExtractor
import io.github.lukebemish.mcdevutils.sided.impl.discovery.SideStringFormatter
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.CacheableTask
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.SourceSet
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.tree.ClassNode

import java.nio.file.Files
import java.util.zip.ZipFile

@CacheableTask
class SidedCheckerTask extends DefaultTask {

    @Input
    SourceSet sources

    // Analyze classes
    @TaskAction
    analyze() {
        MCDevUtilExtension ext = (MCDevUtilExtension)project.getExtensions().getByType(MCDevUtilExtension.class)
        // Setup platform
        switch (ext.platform()) {
            case MCDevUtilExtension.Platform.FORGE : {
                SideStringFormatter.FORMATTER.platform = SideStringFormatter.Platform.FORGE
                break
            }
            case MCDevUtilExtension.Platform.FABRIC : {
                SideStringFormatter.FORMATTER.platform = SideStringFormatter.Platform.FABRIQUILT
                break
            }
            case MCDevUtilExtension.Platform.QUILT : {
                SideStringFormatter.FORMATTER.platform = SideStringFormatter.Platform.FABRIQUILT
                break
            }
        }

        var extractor = new NamingClassExtractor()

        for (File file : sources.output.classesDirs) {
            analyzeFiles(file, extractor)
        }

        for (File file : sources.compileClasspath) {
            if (!file.isDirectory()) {
                ZipFile zipFile = new ZipFile(file)
                zipFile.stream()
                        .filter { it.name.endsWith(".class") }
                        .forEach { classEntry ->
                            var reader = new ClassReader(zipFile
                                    .getInputStream(classEntry).readAllBytes())
                            var visitor = new ClassNode()
                            reader.accept(visitor, 0)
                            extractor.recordDependencies(visitor)
                        }
            } else {
                analyzeFiles(file, extractor)
            }
        }

        extractor.checkElements()
    }

    private analyzeFiles(File file, NamingClassExtractor extractor) {
        if (file.isDirectory()) {
            for (File file1 : file.listFiles()) {
                analyzeFiles(file1, extractor)
            }
        } else {
            if (file.name.endsWith(".class") && !file.isDirectory()) {
                var reader = new ClassReader(Files.readAllBytes(file.toPath()))
                var visitor = new ClassNode()
                reader.accept(visitor, 0)
                extractor.recordDependencies(visitor)
            }
        }
    }
}
