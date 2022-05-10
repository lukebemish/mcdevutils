package io.github.lukebemish.mcdevutils.impl.gradle


import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.artifacts.DependencyResolutionListener
import org.gradle.api.artifacts.ResolvableDependencies
import org.gradle.api.plugins.JavaPluginExtension

class MCDevUtilsPlugin implements Plugin<Project> {
    @Override
    void apply(Project project) {
        JavaPluginExtension javaPluginExtension = (JavaPluginExtension)project.getExtensions().getByType(JavaPluginExtension.class)

        final var ext = project.getExtensions().create(MCDevUtilExtension.NAME, MCDevUtilExtension.class)
        final var injection = new DependencyInjection(project, ext)

        project.afterEvaluate {

            // First, unpack stuff.
            injection.unpackJars()

            // Then, inject deps.
            var compileOnlyDeps = project.getConfigurations().getByName("compileOnly").getDependencies()
            var testCompileOnlyDeps = project.getConfigurations().getByName("testCompileOnly").getDependencies()
            var annotationProcessorDeps = project.getConfigurations().getByName("annotationProcessor").getDependencies()
            project.getGradle().addListener(new DependencyResolutionListener() {
                @Override
                void beforeResolve(ResolvableDependencies resolvableDependencies) {
                    compileOnlyDeps.add(project.getDependencies().create(injection.getFileDependency()))
                    testCompileOnlyDeps.add(project.getDependencies().create(injection.getFileDependency()))
                    annotationProcessorDeps.add(project.getDependencies().create(injection.getFileDependency()))

                    project.getGradle().removeListener(this)
                }

                @Override
                void afterResolve(ResolvableDependencies resolvableDependencies) {}
            })

            // Then, try to attach sources
            injection.attachSources()
        }

        project.pluginManager.withPlugin("java") {
            var task = project.tasks.register('verifySidesTask', SidedCheckerTask) {
                setGroup('Verification')
                setDescription('Analyze project for client/server side constrained code issues.')
                sources = javaPluginExtension.sourceSets.getByName('main')
                dependsOn('classes')
            }

            project.tasks.named('check').configure {
                dependsOn task
            }
        }
    }
}
