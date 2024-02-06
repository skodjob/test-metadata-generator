package io.skodjob;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
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
import java.util.Map;

import static io.skodjob.DocGenerator.generate;
import static io.skodjob.DocGenerator.getTestClassesWithTheirPath;

@Mojo(
        name = "test-docs-generator",
        defaultPhase = LifecyclePhase.COMPILE,
        requiresDependencyResolution = ResolutionScope.COMPILE_PLUS_RUNTIME
)
public class DocGeneratorMojo extends AbstractMojo {

    @Parameter(property = "filePath", defaultValue = "./test", required = true, readonly = true)
    String filePath;

    @Parameter(property = "generatePath", defaultValue = "./test-docs", required = true, readonly = true)
    String generatePath;

    @Parameter( defaultValue = "${project}", readonly = true )
    MavenProject project;

    @Parameter( defaultValue = "${plugin}", readonly = true )
    PluginDescriptor descriptor;

    public void execute() throws MojoExecutionException, MojoFailureException {

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
        }
        catch (MalformedURLException e) {
            e.printStackTrace();
        }

        for (URL url : classRealm.getURLs()) {
            getLog().info(url.getFile());
        }

        Map<String, String> classes = getTestClassesWithTheirPath(filePath, generatePath);

        for (Map.Entry<String, String> entry : classes.entrySet()) {
            try {
                Class<?> testClass = classRealm.loadClass(entry.getValue());
                generate(testClass, entry.getKey() + ".md");
            } catch (ClassNotFoundException | IOException ex) {
                getLog().warn(String.format("Cannot load %s", entry.getValue()));
                getLog().error(ex);
            }
        }

        getLog().info("Done");
    }

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
                } else if (file.isFile() && file.getName().toLowerCase().endsWith(".jar")) {
                    // Print the absolute path if it's a .jar file
                    getLog().info("Found .jar file: " + file.getAbsolutePath());
                    classRealm.addURL(file.toURI().toURL());
                }
            }
        }
    }
}
