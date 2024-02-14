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

/**
 * Utils class with supported method for all kind of generators.
 */
public class Utils {

    private static final Pattern REMOVE_BEFORE_PACKAGE = Pattern.compile(".*java\\/");

    /**
     * Updates Map ({@param classes}) with info about classes inside {@param packagePath}.
     * It goes through all files inside the {@param packagePath} and does two things:
     * - in case that file is directory (another package), it recursively calls itself and adds all info needed for all files
     *   inside the directory
     * - otherwise adds key/value record in the map
     *      - key -> path in which the particular `.md` file will be generated, typically {@param generatePath}/{@code classPackagePath}
     *          - f.e. -> ./test-docs/path/to/my/package/TestClassST
     *          - value -> path for the particular test class -> in package format, available on classpath
     *          - f.e. -> path.to.my.package.TestClassST
     * @param classes Map that should be updated with test-classes info
     * @param packagePath path on which the files and classes should be listed
     * @param generatePath "prefix" path where the documentation should be generated into
     * @return updated Map with test-classes info from the {@param packagePath}
     */
    private static Map<String, String> getClassesForPackage(Map<String, String> classes, Path packagePath, String generatePath) {
        try {
            Files.list(packagePath)
                    .forEach(path -> {
                        if (Files.isDirectory(path)) {
                            classes.putAll(getClassesForPackage(classes, path, generatePath));
                        } else {
                            String classPackagePath = path.toAbsolutePath().toString().replaceAll(REMOVE_BEFORE_PACKAGE.toString(), "").replace(".java", "");
                            classes.put(generatePath + classPackagePath, classPackagePath.replaceAll("/", "."));
                        }
                    });
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return classes;
    }

    /**
     * On specified {@param filePath} lists all classes and returns them in Map, where key is target
     * path, where the Markdown file will be created, and value is package with class name available
     * on classpath
     * Also excludes files that should not be considered for documentation (currently just "AbstractST")
     * @param filePath path where are all test-classes present
     * @param generatePath "prefix" path where the documentation should be generated into
     * @return Map with test-classes info from the {@param filePath}
     */
    public static Map<String, String> getTestClassesWithTheirPath(String filePath, String generatePath) {
        Map<String, String> classes = new HashMap<>();

        try {
            Files.list(Paths.get(filePath))
                    .forEach(path -> classes.putAll(getClassesForPackage(classes, path, generatePath)));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return classes;
    }

    /**
     * Creates needed files and folders for the particular test-suite (test-class)
     * @param classFilePath path where the test-suite (test-class) is present
     * @return file writer
     * @throws IOException during file creation
     */
    public static PrintWriter createFilesForTestClass(String classFilePath) throws IOException {
        String fileName = classFilePath.substring(classFilePath.lastIndexOf('/') + 1);
        String parentPath = classFilePath.replace(fileName, "");

        final File parent = new File(parentPath);
        if (!parent.mkdirs()) {
            System.err.println("Could not create parent directories ");
        }
        final File classFile = new File(parent, fileName);
        classFile.createNewFile();

        FileWriter write = new FileWriter(classFile);
        return new PrintWriter(write);
    }
}
