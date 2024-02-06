/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.TestDoc;
import io.skodjob.markdown.Header;
import io.skodjob.markdown.Table;
import io.skodjob.markdown.TextList;
import io.skodjob.markdown.TextStyle;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Main Test Docs Generator class of the module
 * The DocGenerator generates Markdown files for each documented test-case inside the test-class
 * For each test-class is created separate Markdown file - with the same name as the test-class
 * All needed information is parsed from the particular @TestDoc annotation.
 */
public class DocGenerator {
    private static final Pattern REMOVE_BEFORE_PACKAGE = Pattern.compile(".*java\\/");

    /**
     * Method that generates test documentation of the specified test-class.
     * Lists all methods (test-cases) annotated by {@link TestDoc} inside the {@param testClass}, creates
     * parent folders (if needed), new Markdown file for the class, and finally using {@link #createRecord(PrintWriter, TestDoc, String)} generates
     * documentation for each test-case, written inside the newly created Markdown file.
     *
     * @param testClass for which the Markdown file is created and test-cases are documented
     * @param classFilePath path of the Markdown file
     * @throws IOException during file creation
     */
    public static void generate(Class<?> testClass, String classFilePath) throws IOException {
        List<Method> methods = Arrays.stream(testClass.getDeclaredMethods())
            .filter(method -> method.getAnnotation(TestDoc.class) != null)
            .toList();

        if (!methods.isEmpty()) {
            String fileName = classFilePath.substring(classFilePath.lastIndexOf('/') + 1);
            String parentPath = classFilePath.replace(fileName, "");

            final File parent = new File(parentPath);
            if (!parent.mkdirs()) {
                System.err.println("Could not create parent directories ");
            }
            final File classFile = new File(parent, fileName);
            classFile.createNewFile();

            FileWriter write = new FileWriter(classFile);
            PrintWriter printWriter = new PrintWriter(write);

            printWriter.println(Header.firstLevelHeader(testClass.getSimpleName()));

            methods.forEach(method -> {
                TestDoc testDoc = method.getAnnotation(TestDoc.class);
                if (testDoc != null) {
                    createRecord(printWriter, testDoc, method.getName());
                }
            });

            printWriter.close();
        }
    }

    /**
     * Creates a single record of a test-case (test-method) inside the test-class
     * The record contains: name of the test as header level 2, description, steps, and use-cases obtained from the
     * {@param testDoc}.
     *
     * @param write file writer
     * @param testDoc annotation containing all @TestDoc objects, from which is the record generated
     * @param methodName name of the test-case containing {@param testDoc}
     */
    public static void createRecord(PrintWriter write, TestDoc testDoc, String methodName) {
        write.println();
        write.println(Header.secondLevelHeader(methodName));
        write.println();
        write.println(TextStyle.boldText("Description:") + " " + testDoc.description().value());
        write.println();
        write.println(TextStyle.boldText("Steps:"));
        write.println();
        write.println(createTableOfSteps(testDoc.steps()));
        write.println(TextStyle.boldText("Use-cases:"));
        write.println();
        write.println(TextList.createUnorderedList(createUseCases(testDoc.usecases())));
    }

    /**
     * For the provided list of steps creates table with following columns:
     *  - Step - number of the current step
     *  - Action - action done during the step
     *  - Result - expected result of the step
     *
     * @param steps list of steps of the test-case
     * @return String representation of table in Markdown
     */
    private static String createTableOfSteps(TestDoc.Step[] steps) {
        List<String> tableRows = new ArrayList<>();
        List<String> headers = List.of("Step", "Action", "Result");

        for (int i = 0; i < steps.length; i++) {
            tableRows.add(Table.createRow(i + 1 + ".", steps[i].value(), steps[i].expected()));
        }

        return Table.createTable(headers, tableRows);
    }

    /**
     * Creates list of use-cases for the particular test-case in format - `use-case`
     * @param usecases list of usecases from the {@link TestDoc} annotation
     * @return list of usecases in {@link List<String>}
     */
    private static List<String> createUseCases(TestDoc.Usecase[] usecases) {
        List<String> usesText = new ArrayList<>();
        Arrays.stream(usecases).forEach(usecase -> usesText.add("`" + usecase.id() + "`"));

        return usesText;
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
    static Map<String, String> getTestClassesWithTheirPath(String filePath, String generatePath) {
        Map<String, String> classes = new HashMap<>();

        try {
            Files.list(Paths.get(filePath))
                .filter(file -> !file.getFileName().toString().contains("AbstractST"))
                .forEach(path -> classes.putAll(getClassesForPackage(classes, path, generatePath)));
        } catch (IOException exception) {
            throw new RuntimeException(exception);
        }

        return classes;
    }

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
}
