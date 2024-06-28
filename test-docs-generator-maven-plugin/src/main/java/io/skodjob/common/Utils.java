/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.common;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Utils class with supported method for all kind of generators.
 */
public class Utils {

    private static final Pattern REMOVE_BEFORE_PACKAGE = Pattern.compile(".*java/");

    /**
     * Constructor
     */
    private Utils() {
        // constructor
    }

    /**
     * Updates Map ({@param classes}) with info about classes inside {@param packagePath}.
     * It goes through all files inside the {@param packagePath} and does two things:
     * <ul><li>in case that file is directory (another package), it recursively calls itself and adds all info needed for all files inside the directory</li>
     * <li>otherwise adds key/value record in the map
     * <ul><li>key -> path in which the particular `.md` file will be generated, typically {@param generatePath}/{@code classPackagePath}
     * <ul><li>f.e. -> ./test-docs/path/to/my/package/TestClassST</li>
     * <li>value -> path for the particular test class -> in package format, available on classpath</li>
     * <li>f.e. -> path.to.my.package.TestClassST</li></ul></li></ul></li></ul>
     *
     * @param classes     Map that should be updated with test-classes info
     * @param packagePath path on which the files and classes should be listed
     * @return updated Map with test-classes info from the {@param packagePath}
     */
    private static Map<String, String> getClassesForPackage(Map<String, String> classes, Path packagePath, boolean generateDirs) {
        if (Files.isDirectory(packagePath)) {
            try (Stream<Path> pathStream = Files.list(packagePath)) {
                pathStream.forEach(path -> classes.putAll(getClassesForPackage(classes, path, generateDirs)));
            } catch (IOException e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        } else {
            String classPackagePath = packagePath.toAbsolutePath().toString().replaceAll(REMOVE_BEFORE_PACKAGE.toString(), "").replace(".java", "");
            String filename = classPackagePath;
            if (!generateDirs) {
                filename = filename.replaceAll("/", ".");
            }
            classes.put(filename, classPackagePath.replaceAll("/", "."));
        }

        return classes;
    }

    /**
     * On specified {@param filePath} lists all classes and returns them in Map, where key is target
     * path, where the Markdown file will be created, and value is package with class name available
     * on classpath
     * Also excludes files that should not be considered for documentation (currently just "AbstractST")
     *
     * @param filePath     path where are all test-classes present
     * @param generateDirs whether it should generate subfolders for packages or not
     * @return Map with test-classes info from the {@param filePath}
     */
    public static Map<String, String> getTestClassesWithTheirPath(String filePath, boolean generateDirs) {
        Map<String, String> classes = new HashMap<>();

        try (Stream<Path> pathStream = Files.list(Paths.get(filePath))) {
            pathStream.forEach(path -> classes.putAll(getClassesForPackage(classes, path, generateDirs)));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return classes;
    }

    /**
     * Creates needed files and folders for the particular test-suite (test-class)
     *
     * @param classFilePath path where the test-suite (test-class) is present
     * @return file writer
     * @throws IOException during file creation
     */
    public static PrintWriter createFilesForTestClass(String classFilePath) throws IOException {
        String fileName = classFilePath.substring(classFilePath.lastIndexOf('/') + 1);
        String parentPath = classFilePath.replace(fileName, "");

        final File parent = new File(parentPath);
        if (!parent.exists()) {
            parent.mkdirs();
        }

        final File classFile = new File(parent, fileName);
        classFile.createNewFile();

        FileWriter write = new FileWriter(classFile);
        return new PrintWriter(write);
    }
}
