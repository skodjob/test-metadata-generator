/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.common.Utils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Locale;
import java.util.Map;

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

    /**
     * Descriptor of the plugin
     * Defaults to current plugin
     */
    @Parameter(defaultValue = "${plugin}", readonly = true)
    PluginDescriptor descriptor;

    /**
     * Method for the execution of the test-docs-generator Maven plugin
     * Generates documentation of test-cases based on specified parameters:
     * <ul><li>{@link #testsPath}</li>
     * <li>{@link #docsPath}</li>
     * <li>{@link #project}</li>
     * <li>{@link #descriptor}</li></ul>
     */
    public void execute() {

        getLog().info("Starting generator");

        final ClassRealm classRealm = descriptor.getClassRealm();

        try {
            // Add target/classes
            File classesFiles = new File(project.getBuild().getOutputDirectory());
            classRealm.addURL(classesFiles.toURI().toURL());
            // Add target/test-classes
            classesFiles = new File(project.getBuild().getTestOutputDirectory());
            classRealm.addURL(classesFiles.toURI().toURL());
            // Add all jar files in target lib
            addJarFilesToClassPath(new File(project.getBuild().getDirectory()), classRealm);
        } catch (MalformedURLException e) {
            getLog().error(e);
        }

        getLog().debug("Loaded files in classpath:");
        for (URL url : classRealm.getURLs()) {
            getLog().debug(url.getFile());
        }

        // Ensure that docsPath ends with /
        if (!docsPath.endsWith("/")) {
            docsPath += "/";
        }

        Map<String, String> classes = Utils.getTestClassesWithTheirPath(testsPath, generateDirs);
        for (Map.Entry<String, String> entry : classes.entrySet()) {
            try {
                Class<?> testClass = classRealm.loadClass(entry.getValue());
                // In case user don't want to generate fmf, the md file won't be in md folder
                String mdDirectoryName = "";
                if (generateFmf) {
                    // Add md folder to the path
                    mdDirectoryName = "md/";
                    FmfGenerator.generate(testClass, docsPath + "fmf/" + entry.getKey() + ".fmf");
                } else {
                    getLog().debug("Skipping fmf generation");
                }
                MdGenerator.generate(testClass, docsPath, mdDirectoryName + entry.getKey() + ".md");

            } catch (ClassNotFoundException | IOException ex) {
                getLog().warn(String.format("Cannot load %s", entry.getValue()));
                getLog().error(ex);
            }
        }

        MdGenerator.updateLinksInLabels(docsPath);

        getLog().info("Done");
    }

    /**
     * It goes through all files inside the {@param directory} and does two things:
     * <ul><li>in case that file is directory (another package), it recursively calls itself and adds all .jar files to the classPath</li>
     * <li>otherwise adds the .jar files to classPath</li></ul>
     *
     * @param directory  where the files are listed
     * @param classRealm realm containing all libs set on classPath, from where the test-classes will be loaded
     * @throws MalformedURLException during URL construction
     */
    public void addJarFilesToClassPath(File directory, ClassRealm classRealm) throws MalformedURLException {
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
                    addJarFilesToClassPath(file, classRealm);
                } else if (file.isFile() && file.getName().toLowerCase(Locale.ROOT).endsWith(".jar")) {
                    // Print the absolute path if it's a .jar file
                    getLog().debug("Found .jar file: " + file.getAbsolutePath());
                    classRealm.addURL(file.toURI().toURL());
                }
            }
        }
    }
}
