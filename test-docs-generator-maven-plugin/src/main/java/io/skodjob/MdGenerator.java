/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.Label;
import io.skodjob.annotations.Step;
import io.skodjob.annotations.SuiteDoc;
import io.skodjob.annotations.TestDoc;
import io.skodjob.common.Utils;
import io.skodjob.markdown.Header;
import io.skodjob.markdown.Line;
import io.skodjob.markdown.Table;
import io.skodjob.markdown.TextList;
import io.skodjob.markdown.TextStyle;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;

/**
 * The MdGenerator generates Markdown files for each documented test-case inside the test-class
 * For each test-class is created separate Markdown file - with the same name as the test-class
 * All needed information is parsed from the particular @TestDoc annotation.
 */
public class MdGenerator {

    private static final String LABELS = "labels";
    private static Map<String, Map<String, String>> labelsMap = new TreeMap<>();

    /**
     * Private Constructor
     */
    private MdGenerator() {
        // constructor
    }

    /**
     * Method that generates test documentation of the specified test-class.
     * Lists all methods (test-cases) annotated by {@link TestDoc} inside the {@param testClass}, creates
     * parent folders (if needed), new Markdown file for the class, and after that generates test-suite documentation using
     * {@link #createSuiteRecord(PrintWriter, String, String, SuiteDoc)} and then test-cases documentation using {@link #createTestRecord(PrintWriter, TestDoc, String, String, String)},
     * all written inside the newly created Markdown file.
     *
     * @param testClass     for which the Markdown file is created and test-cases are documented
     * @param docsDirPath   root path of the testing docs
     * @param classFilePath path of the Markdown file
     * @throws IOException during file creation
     */
    public static void generate(Class<?> testClass, String docsDirPath, String classFilePath) throws IOException {
        SuiteDoc suiteDoc = testClass.getAnnotation(SuiteDoc.class);
        String classFilePathFull = docsDirPath + classFilePath;

        List<Method> methods = Arrays.stream(testClass.getDeclaredMethods())
            .filter(method -> method.getAnnotation(TestDoc.class) != null)
            .sorted(Comparator.comparing((Method m) -> m.getName().toLowerCase(Locale.ENGLISH))).toList();

        if (suiteDoc != null || !methods.isEmpty()) {
            try (PrintWriter printWriter = Utils.createFilesForTestClass(classFilePathFull)) {
                // creating first level header for the test-suite
                printWriter.println(Header.firstLevelHeader(testClass.getSimpleName()));

                String labelsFilesPath = computePathToLabelFiles(classFilePath);

                generateDocumentationForTestSuite(printWriter, labelsFilesPath, classFilePathFull, suiteDoc);
                generateDocumentationForTestCases(printWriter, labelsFilesPath, classFilePathFull, methods);
            }
        }
    }

    /**
     * Generates documentation for the test-suite (test-class) if {@link SuiteDoc} is present
     *
     * @param writer          file writer
     * @param labelsFilesPath relative path to directory with labels files
     * @param classFilePath   path to generated class file doc
     * @param suiteDoc        containing {@link SuiteDoc} annotation
     */
    private static void generateDocumentationForTestSuite(PrintWriter writer, String labelsFilesPath, String classFilePath, SuiteDoc suiteDoc) {
        if (suiteDoc != null) {
            createSuiteRecord(writer, labelsFilesPath, classFilePath, suiteDoc);
        }
    }

    /**
     * Generates documentation records for each test-cases (test-methods) from {@param methods}
     *
     * @param writer          file writer
     * @param labelsFilesPath relative path to directory with labels files
     * @param methods         containing {@link TestDoc} annotation
     */
    private static void generateDocumentationForTestCases(PrintWriter writer, String labelsFilesPath, String classFilePath, List<Method> methods) {

        if (!methods.isEmpty()) {
            methods.forEach(method -> {
                TestDoc testDoc = method.getAnnotation(TestDoc.class);
                if (testDoc != null) {
                    createTestRecord(writer, testDoc, labelsFilesPath, classFilePath, method.getName());
                }
            });
        }
    }

    /**
     * Creates a single record of a test-case (test-method) inside the test-class
     * The record contains: name of the test as header level 2, description, steps, and use-cases obtained from the
     * {@param testDoc}.
     *
     * @param write           file writer
     * @param testDoc         annotation containing all @TestDoc objects, from which is the record generated
     * @param labelsFilesPath relative path to directory with labels files
     * @param classFilePath   path to generated class file doc
     * @param methodName      name of the test-case containing {@param testDoc}
     */
    public static void createTestRecord(PrintWriter write, TestDoc testDoc, String labelsFilesPath, String classFilePath, String methodName) {
        write.println();
        write.println(Header.secondLevelHeader(methodName));
        write.println();
        write.println(TextStyle.boldText("Description:") + " " + testDoc.description().value());
        write.println();
        if (!Objects.equals(testDoc.contact().name(), "")) {
            write.println(TextStyle.boldText("Contact:") + " `" + testDoc.contact().name() + " <" + testDoc.contact().email() + ">`");
            write.println();
        }

        if (testDoc.steps().length != 0) {
            write.println(TextStyle.boldText("Steps:"));
            write.println();
            write.println(createTableOfSteps(testDoc.steps()));
        }

        if (testDoc.labels().length != 0) {
            write.println(TextStyle.boldText("Labels:"));
            write.println();

            List<String> labelsWithLinks = createLabelsLink(testDoc.labels(), labelsFilesPath, classFilePath);

            write.println(TextList.createUnorderedList(labelsWithLinks));
            Arrays.stream(testDoc.labels()).forEach(label -> {
                // Get the existing list or create a new one if the key doesn't exist
                Map<String, String> existingMap = labelsMap.getOrDefault(label.value(), new TreeMap<>());
                // Add the new value to the list
                existingMap.put(methodName, classFilePath);

                // Put the updated list back into the map
                labelsMap.put(label.value(), existingMap);
            });
        }
    }

    /**
     * Creates a single record for a test-suite (test-class)
     * The record contains: description, before tests execution steps, after tests execution steps, and use-cases obtained from the
     * {@param suiteDoc}.
     *
     * @param write           file writer
     * @param labelsFilesPath relative path to directory with labels files
     * @param classFilePath   path to generated class file doc
     * @param suiteDoc        annotation containing all @SuiteDoc objects, from which is the record generated
     */
    public static void createSuiteRecord(PrintWriter write, String labelsFilesPath, String classFilePath, SuiteDoc suiteDoc) {
        write.println();
        write.println(TextStyle.boldText("Description:") + " " + suiteDoc.description().value());
        write.println();
        if (!Objects.equals(suiteDoc.contact().name(), "")) {
            write.println(TextStyle.boldText("Contact:") + " `" + suiteDoc.contact().name() + " <" + suiteDoc.contact().email() + ">`");
            write.println();
        }

        if (suiteDoc.beforeTestSteps().length != 0) {
            write.println(TextStyle.boldText("Before test execution steps:"));
            write.println();
            write.println(createTableOfSteps(suiteDoc.beforeTestSteps()));
        }

        if (suiteDoc.afterTestSteps().length != 0) {
            write.println(TextStyle.boldText("After test execution steps:"));
            write.println();
            write.println(createTableOfSteps(suiteDoc.afterTestSteps()));
        }

        if (suiteDoc.labels().length != 0) {
            write.println(TextStyle.boldText("Labels:"));
            write.println();
            write.println(TextList.createUnorderedList(createLabelsLink(suiteDoc.labels(), labelsFilesPath, classFilePath)));
        }

        write.println(Line.horizontalLine());
    }

    /**
     * For the provided list of steps creates table with following columns:
     * <ul>Step - number of the current step</li>
     * <li>Action - action done during the step</li>
     * <li>Result - expected result of the step</li></ul>
     *
     * @param steps list of steps of the test-case
     * @return String representation of table in Markdown
     */
    private static String createTableOfSteps(Step[] steps) {
        List<String> tableRows = new ArrayList<>();
        List<String> headers = List.of("Step", "Action", "Result");

        for (int i = 0; i < steps.length; i++) {
            tableRows.add(Table.createRow(i + 1 + ".", steps[i].value(), steps[i].expected()));
        }

        return Table.createTable(headers, tableRows);
    }

    /**
     * Creates list of labels for the particular test-case in format - `label`
     *
     * @param labels list of labels from the {@link TestDoc} annotation
     * @return list of labels in {@link List<String>}
     */
    private static List<String> createLabels(Label[] labels) {
        List<String> usesText = new ArrayList<>();
        Arrays.stream(labels).forEach(testLabel -> usesText.add("`" + testLabel.value() + "`"));

        return usesText;
    }

    /**
     * Creates list of full labels related data pasted to md file.
     *
     * @param labels         list of labels
     * @param labelsFilePath path to directory with labels description
     * @param classFilePath  path to final md file
     * @return list of final String for each label
     */
    private static List<String> createLabelsLink(Label[] labels, String labelsFilePath, String classFilePath) {
        List<String> labelsWithLinks = new ArrayList<>();

        Arrays.stream(labels).forEach(label -> {
            String pureLabel = label.value().replace("`", "");
            Path path = Paths.get(classFilePath);

            if (new File(path.getParent() + "/" + labelsFilePath + "/" + pureLabel + ".md").exists()) {
                labelsWithLinks.add("[" + pureLabel + "](" + labelsFilePath + "/" + pureLabel + ".md)");
            } else {
                labelsWithLinks.add("`" + pureLabel + "` (description file doesn't exist)");
            }
        });

        return labelsWithLinks;
    }

    /**
     * Update label file that is available in the docs dir
     *
     * @param labelFilePath path to label file within docs dir
     * @param updatedData   data that will be put into the file
     */
    private static void updateLabelFile(String labelFilePath, String updatedData) {
        try {
            File markdownFile = new File(labelFilePath);
            StringBuilder fileContent = new StringBuilder();
            boolean foundGeneratedPart = false;

            try (BufferedReader reader = new BufferedReader(new FileReader(markdownFile, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains("<!-- generated part -->")) {
                        foundGeneratedPart = true;
                        fileContent.append(line).append("\n");
                        break;
                    }
                    fileContent.append(line).append("\n");
                }
            }

            if (!foundGeneratedPart) {
                fileContent.append("\n<!-- generated part -->\n");
            }
            // Append the new content
            fileContent.append(updatedData).append("\n");
            // Write the updated content back to the file
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(markdownFile, StandardCharsets.UTF_8))) {
                writer.write(fileContent.toString());
            }
            System.out.println("Content of %s updated successfully!".formatted(labelFilePath));

        } catch (IOException e) {
            throw new RuntimeException("Failed to update label file: " + labelFilePath, e);
        }
    }

    /**
     * Updates links in existing labels files with corresponding testcases that covers the use-case
     *
     * @param docsPath path where all test docs are stored
     */
    public static void updateLinksInLabels(String docsPath) {
        String labelsPath = docsPath + LABELS;

        if (Files.exists(new File(labelsPath).toPath())) {
            for (Map.Entry<String, Map<String, String>> entry : MdGenerator.labelsMap.entrySet()) {
                String labelsFile = labelsPath + "/" + entry.getKey() + ".md";

                if (Files.exists(new File(labelsFile).toPath())) {
                    StringBuilder newText = new StringBuilder("**Tests:**");
                    for (Map.Entry<String, String> item : entry.getValue().entrySet()) {
                        String data = String.format("[%s](%s%s)", item.getKey(), "../", item.getValue().replace(docsPath, ""));
                        newText.append("\n- ").append(data);
                    }

                    MdGenerator.updateLabelFile(labelsFile, newText.toString());
                } else {
                    System.out.printf("Label file %s doesn't exists. Skipping it.%n", labelsFile);
                }
            }
        }
    }

    /**
     * Compute a relative path to the directory that contains Labels description based on md doc file
     *
     * @param docFilePath
     * @return relative path form doc file to labels directory
     */
    private static String computePathToLabelFiles(String docFilePath) {
        int numberOfDirs = docFilePath.length() - docFilePath.replace("/", "").length();
        return "../".repeat(numberOfDirs) + LABELS;
    }
}
