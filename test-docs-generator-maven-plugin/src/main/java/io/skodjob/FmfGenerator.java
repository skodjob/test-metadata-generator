/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.Label;
import io.skodjob.annotations.Step;
import io.skodjob.annotations.TestDoc;
import io.skodjob.common.Utils;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


/**
 * The FmfGenerator generates FMF files for each documented test-case inside the test-class
 * For each test-class is created separate FMF file - with the same name as the test-class
 * All needed information is parsed from the particular @TestDoc annotation.
 */
public class FmfGenerator {

    /**
     * Private Constructor
     */
    private FmfGenerator() {
        // constructor
    }

    /**
     * Generates test documentation in fmf format.
     * Lists all methods (test-cases) annotated by {@link TestDoc} inside the {@param testClass}, creates
     * parent folders (if needed), new FMF file for the class, and after that generates test-suite documentation using
     * {@link #generateDocumentationForTestCases(PrintWriter, List)}, all written inside the newly created Markdown file.
     *
     * @param testClass     for which the FMF file is created and test-cases are documented
     * @param classFilePath path of the FMF file
     * @throws IOException during file creation
     */
    public static void generate(Class<?> testClass, String classFilePath) throws IOException {
        List<Method> methods = Arrays.stream(testClass.getDeclaredMethods())
            .filter(method -> method.getAnnotation(TestDoc.class) != null)
            .sorted(Comparator.comparing(Method::getName)).toList();

        if (!methods.isEmpty()) {
            PrintWriter printWriter = Utils.createFilesForTestClass(classFilePath);

            generateDocumentationForTestCases(printWriter, methods);

            printWriter.close();
        }
    }

    /**
     * Generates documentation records for each test-cases (test-methods) from {@param methods}
     *
     * @param writer  file writer
     * @param methods containing {@link TestDoc} annotation
     */
    private static void generateDocumentationForTestCases(PrintWriter writer, List<Method> methods) {
        if (!methods.isEmpty()) {
            methods.forEach(method -> {
                TestDoc testDoc = method.getAnnotation(TestDoc.class);
                if (testDoc != null) {
                    createTestRecord(writer, testDoc, method.getName());
                }
            });
        }
    }

    /**
     * Creates a single record of a test-case (test-method) inside the test-class
     * The record contains: name of the test, description, steps, labels, and use-cases obtained from the
     * {@param testDoc}.
     *
     * @param write      file writer
     * @param testDoc    annotation containing all @TestDoc objects, from which is the record generated
     * @param methodName name of the test-case containing {@param testDoc}
     */
    public static void createTestRecord(PrintWriter write, TestDoc testDoc, String methodName) {

        Map<String, Object> testCaseData = new HashMap<>();
        testCaseData.put("summary", methodName);
        // Make description multiline out of the box
        String description = String.format("%s\n", testDoc.description().value());
        testCaseData.put("description", description);
        if (!Objects.equals(testDoc.contact().name(), "")) {
            testCaseData.put("contact", String.format("%s <%s>", testDoc.contact().name(), testDoc.contact().email()));
        }
        if (testDoc.labels().length > 0) {
            testCaseData.put("labels", createLabels(testDoc.labels()));
        }
        if (testDoc.steps().length > 0) {
            testCaseData.put("steps", createListOfSteps(testDoc.steps()));
        }

        Map<String, Object> yamlData = new HashMap<>();
        String testCaseKey = String.format("/%s", methodName);
        yamlData.put(testCaseKey, testCaseData);

        // Serialize the data to YAML
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
        Yaml yaml = new Yaml(options);

        yaml.dump(yamlData, write);
        write.println();
    }

    /**
     * For the provided list of steps creates table with following columns:
     *
     * @param steps list of steps of the test-case
     * @return List of maps that represents all steps of teh test-case
     */
    private static List<Map<String, String>> createListOfSteps(Step[] steps) {

        List<Map<String, String>> listOfSteps = new ArrayList<>();

        for (Step step : steps) {
            Map<String, String> stepMap = new HashMap<>();
            stepMap.put("step", step.value());
            stepMap.put("result", step.expected());
            listOfSteps.add(stepMap);
        }

        return listOfSteps;
    }

    /**
     * Creates list of labels for the particular test-case
     *
     * @param labels list of labels from the {@link TestDoc} annotation
     * @return list of labels in {@link List<String>}
     */
    private static List<String> createLabels(Label[] labels) {
        List<String> fmfLabels = new ArrayList<>();
        Arrays.stream(labels).forEach(testLabel -> fmfLabels.add(testLabel.value()));

        return fmfLabels;
    }
}
