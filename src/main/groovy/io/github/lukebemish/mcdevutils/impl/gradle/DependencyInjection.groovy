package io.github.lukebemish.mcdevutils.impl.gradle;

import org.gradle.api.Project;
import org.gradle.api.file.ConfigurableFileCollection;
import org.gradle.plugins.ide.eclipse.model.Classpath;
import org.gradle.plugins.ide.eclipse.model.EclipseModel;
import org.gradle.plugins.ide.eclipse.model.Library;
import org.gradle.plugins.ide.idea.IdeaPlugin;
import org.gradle.plugins.ide.idea.model.ModuleLibrary;

import java.nio.file.Files;
import java.nio.file.Path;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

class DependencyInjection {
    private static final int BUFFER_SIZE = 8192;

    public final Project project
    private final MCDevUtilExtension extension
    private final Path cachePath

    MCDevUtilExtension.Platform getPlatform() {
        extension.platform()
    }

    void unpackJars() {
        Path classesPath = getJarPath(null);
        Path sourcesPath = getJarPath("sources");

        if (notMatching(getInternalJarPath(null), classesPath)) {
            try (InputStream isClasses = DependencyInjection.class.getResourceAsStream(getInternalJarPath(null))) {
                if (isClasses == null) throw new IOException("Couldn't find jar for platform "+platform+" at "+getInternalJarPath(null));
                if (!Files.exists(cachePath)) {
                    Files.createDirectories(cachePath);
                }
                try (FileOutputStream outputStream = new FileOutputStream(classesPath.toFile(), false)) {
                    int read;
                    byte[] bytes = new byte[BUFFER_SIZE];
                    while ((read = isClasses.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }

        if (notMatching(getInternalJarPath("sources"), sourcesPath)) {
            try (InputStream isSources = DependencyInjection.class.getResourceAsStream(getInternalJarPath("sources"))) {
                if (isSources == null) throw new IOException("Couldn't find sources jar for platform "+platform+" at "+getInternalJarPath("sources"));
                if (!Files.exists(cachePath)) {
                    Files.createDirectories(cachePath);
                }
                try (FileOutputStream outputStream = new FileOutputStream(sourcesPath.toFile(), false)) {
                    int read;
                    byte[] bytes = new byte[BUFFER_SIZE];
                    while ((read = isSources.read(bytes)) != -1) {
                        outputStream.write(bytes, 0, read);
                    }
                }
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        }
    }

    private static boolean notMatching(String internal, Path external) {
        if (!Files.exists(external) || Files.isDirectory(external)) return true;
        try {
            FileInputStream ex = new FileInputStream(external.toFile())
            byte[] exHash = checksum(ex)
            InputStream is = DependencyInjection.class.getResourceAsStream(internal)
            if (is==null) return false //in this case, we don't want to try and copy the input...
            byte[] inHash = checksum(is)
            return !MessageDigest.isEqual(exHash, inHash)
        } catch (IOException | NoSuchAlgorithmException e) {
            return true;
        }
    }

    private static byte[] checksum(InputStream is) throws NoSuchAlgorithmException, IOException {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        byte[] buffer = new byte[BUFFER_SIZE];
        int numOfBytesRead;
        while( (numOfBytesRead = is.read(buffer)) > 0){
            md.update(buffer, 0, numOfBytesRead);
        }
        return md.digest();
    }

    DependencyInjection(Project project, MCDevUtilExtension extension) {
        this.cachePath = project.getProjectDir().toPath().resolve(".gradle").resolve(Constants.CACHE_FOLDER).toAbsolutePath();
        this.extension = extension;
        this.project = project;

    }

    ConfigurableFileCollection getFileDependency() {
        return project.files(getJarPath(null));
    }
    Path getJarPath(String classifier) {
        return cachePath.resolve(platform.toString() + "-" + Constants.VERSION + (classifier==null?"":("-" + classifier)) + ".jar");
    }

    private String getInternalJarPath(String classifier) {
        return "/bundled/"+platform.toString() + "-" + Constants.VERSION + (classifier==null?"":("-" + classifier)) + ".jar";
    }

    void attachSources() {
        final var jarName = cachePath.relativize(getJarPath(null)).toString();
        final var sourcesJar = getJarPath("sources").toAbsolutePath();

        if (project.getPlugins().hasPlugin("eclipse")) {
            final var eclipse = project.getExtensions().getByType(EclipseModel.class);
            eclipse.classpath(cp -> cp.file(file -> file.whenMerged((Classpath classpath) -> classpath.getEntries().stream()
                    .filter(Library.class::isInstance)
                    .map(Library.class::cast)
                    .filter(lib -> lib.getPath().contains(jarName))
                    .filter(lib -> lib.getSourcePath() == null)
                    .findFirst()
                    .ifPresent(lib -> lib.setSourcePath(classpath.fileReference(sourcesJar))))));
        }

        if (project.getPlugins().hasPlugin("idea")) {
            final var idea = project.getPlugins().getPlugin(IdeaPlugin.class);
            idea.getModel().module(mod -> mod.iml(iml -> iml.whenMerged($unused -> idea.getModel().getModule().resolveDependencies()
                    .stream()
                    .filter(ModuleLibrary.class::isInstance)
                    .map(ModuleLibrary.class::cast)
                    .filter(lib -> lib.getClasses().stream().anyMatch(p -> p.getUrl().contains(jarName)))
                    .filter(lib -> lib.getSources().isEmpty())
                    .findFirst()
                    .ifPresent(lib -> lib.getSources().add(new org.gradle.plugins.ide.idea.model.Path("jar://" + sourcesJar + "!/"))))));
        }
    }
}
