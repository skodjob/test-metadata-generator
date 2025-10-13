/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.common.Utils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.resolver.ArtifactResolutionRequest;
import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.DefaultProjectBuildingRequest;
import org.apache.maven.project.MavenProject;
import org.apache.maven.project.ProjectBuildingRequest;
import org.apache.maven.repository.RepositorySystem;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilder;
import org.apache.maven.shared.dependency.graph.DependencyGraphBuilderException;
import org.apache.maven.shared.dependency.graph.DependencyNode;

import javax.inject.Inject;

import java.io.File;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.List;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Stream;

/**
 * DocGeneratorMojo class for Maven plugin handling
 */
@Mojo(
    name = "test-docs-generator",
    defaultPhase = LifecyclePhase.COMPILE,
    requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class DocGeneratorMojo extends AbstractMojo {

    /**
     * Constructor
     */
    public DocGeneratorMojo() {
        // constructor
    }

    /**
     * Whether project dependencies should be added to the class path
     */
    @Parameter(property = "includeDependencies", defaultValue = "false", required = false, readonly = false)
    boolean includeDependencies;

    /**
     * Directory path where class path libraries can be found
     */
    @Parameter(property = "libraryPath", defaultValue = "${project.build.directory}", required = false, readonly = false)
    File libraryPath;

    /**
     * Path where are all test-classes stored
     */
    @Parameter(property = "testsPath", defaultValue = "./test", required = true, readonly = false)
    String testsPath;

    /**
     * Path where the test documentation should be generated to
     */
    @Parameter(property = "docsPath", defaultValue = "./test-docs", required = true, readonly = false)
    String docsPath;

    /**
     * Whether it should generate subfolders for packages or not
     */
    @Parameter(property = "generateDirs", defaultValue = "false", required = false, readonly = false)
    boolean generateDirs;

    /**
     * Option for generating fmf
     */
    @Parameter(property = "generateFmf", defaultValue = "false", readonly = false)
    boolean generateFmf;

    /**
     * Pointer to Maven project
     * Defaults to current project
     */
    @Parameter(defaultValue = "${project}", readonly = true)
    MavenProject project;

    @Parameter(defaultValue = "${session}", readonly = true, required = true)
    MavenSession session;

    @Inject
    DependencyGraphBuilder dependencyGraphBuilder;

    @Inject
    RepositorySystem repositorySystem;

    /**
     * Method for the execution of the test-docs-generator Maven plugin
     * Generates documentation of test-cases based on specified parameters:
     * <ul><li>{@link #testsPath}</li>
     * <li>{@link #docsPath}</li>
     * <li>{@link #project}</li></ul>
     */
    @Override
    public void execute() throws MojoExecutionException {

        getLog().info("Starting generator");

        Set<URI> classpath = new TreeSet<>();

        // Add target/classes
        File classesFiles = new File(project.getBuild().getOutputDirectory());
        classpath.add(classesFiles.toURI());

        // Add target/test-classes
        classesFiles = new File(project.getBuild().getTestOutputDirectory());
        classpath.add(classesFiles.toURI());

        // Add all jar files in target lib
        addJarFilesToClassPath(libraryPath, classpath);

        if (includeDependencies) {
            // Add project dependencies
            addDependenciesToClassPath(classpath);
        }

        getLog().debug("Loaded files in classpath:");
        for (URI uri : classpath) {
            getLog().debug(uri.getPath());
        }

        // Ensure that docsPath ends with /
        if (!docsPath.endsWith("/")) {
            docsPath += "/";
        }

        URL[] locators = classpath.stream()
                .map(uri -> {
                    try {
                        return uri.toURL();
                    } catch (MalformedURLException e) {
                        getLog().error(e);
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .toArray(URL[]::new);

        try (URLClassLoader loader = new URLClassLoader(locators, Thread.currentThread().getContextClassLoader())) {
            for (var entry : Utils.getTestClassesWithTheirPath(testsPath, generateDirs).entrySet()) {
                generate(loader, entry.getKey(), entry.getValue());
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }

        MdGenerator.updateLinksInLabels(docsPath);

        getLog().info("Done");
    }

    private void generate(ClassLoader loader, String filename, String className) {
        try {
            Class<?> testClass = loader.loadClass(className);
            // In case user don't want to generate fmf, the md file won't be in md folder
            String mdDirectoryName = "";
            if (generateFmf) {
                // Add md folder to the path
                mdDirectoryName = "md/";
                FmfGenerator.generate(testClass, docsPath + "fmf/" + filename + ".fmf");
            } else {
                getLog().debug("Skipping fmf generation");
            }
            MdGenerator.generate(testClass, docsPath, mdDirectoryName + filename + ".md");

        } catch (ClassNotFoundException | IOException ex) {
            getLog().warn(String.format("Cannot load %s", className));
            getLog().error(ex);
        }
    }

    /**
     * It goes through all files inside the {@param directory} and does two things:
     * <ul><li>in case that file is directory (another package), it recursively calls itself and adds all .jar files to the classPath</li>
     * <li>otherwise adds the .jar files to classPath</li></ul>
     *
     * @param directory  where the files are listed
     * @param classpath set containing all libs set on classPath, from where the test-classes will be loaded
     */
    public void addJarFilesToClassPath(File directory, Set<URI> classpath) {
        // Check if the provided file is a directory
        if (!directory.isDirectory()) {
            getLog().warn("Provided file is not a directory.");
            return;
        }

        // List all files and subdirectories in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    // Recursively call the method for subdirectories
                    addJarFilesToClassPath(file, classpath);
                } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                    // Print the absolute path if it's a .jar file
                    getLog().debug("Found .jar file: " + file.getAbsolutePath());
                    classpath.add(file.toURI());
                }
            }
        }
    }

    private void addDependenciesToClassPath(Set<URI> classpath) throws MojoExecutionException {
        ProjectBuildingRequest buildingRequest =
                new DefaultProjectBuildingRequest(session.getProjectBuildingRequest());

        buildingRequest.setProject(project);
        buildingRequest.setResolveDependencies(true);

        DependencyNode projectRoot;

        try {
            projectRoot = dependencyGraphBuilder.buildDependencyGraph(buildingRequest, null);
        } catch (DependencyGraphBuilderException e) {
            throw new MojoExecutionException("Cannot build project dependency graph", e);
        }

        getLog().debug("Project root node %s".formatted(projectRoot.toNodeString()));

        walk(projectRoot.getChildren())
                .map(DependencyNode::getArtifact)
                .map(this::resolve)
                .map(Artifact::getFile)
                .filter(Objects::nonNull)
                .map(File::toURI)
                .forEach(classpath::add);
    }

    /**
     * Flatten all dependency nodes and descendants to a stream.
     */
    private Stream<DependencyNode> walk(List<DependencyNode> nodes) {
        return nodes.stream().flatMap(node -> Stream.concat(Stream.of(node), walk(node.getChildren())));
    }

    /**
     * Attempt to resolve the artifact's file if not present using the repository.
     */
    private Artifact resolve(Artifact artifact) {
        if (artifact.getFile() == null) {
            getLog().debug("Resolving artifact: %s".formatted(artifact));
            var artifactRequest = new ArtifactResolutionRequest();
            artifactRequest.setArtifact(artifact);
            var result = repositorySystem.resolve(artifactRequest);
            // Request only asked for a single artifact, so grab it from the results, defaulting to the original
            return result.getArtifacts().stream().findFirst().orElse(artifact);
        }
        return artifact;
    }
}
