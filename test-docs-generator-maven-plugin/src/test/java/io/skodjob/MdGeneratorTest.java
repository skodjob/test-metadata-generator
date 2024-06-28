/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.Contact;
import io.skodjob.annotations.Desc;
import io.skodjob.annotations.Label;
import io.skodjob.annotations.Step;
import io.skodjob.annotations.SuiteDoc;
import io.skodjob.annotations.TestDoc;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class MdGeneratorTest {
    @Test
    void testCreateTableOfSteps() throws IOException {
        String expectedFilePath = MdGeneratorTest.class.getClassLoader().getResource("expected-docs.md").getPath();
        String generatedFilePath = "target/io/test.md";
        MdGenerator.generate(DummyTest.class, generatedFilePath);

        assertThat(compareFiles(expectedFilePath, generatedFilePath), is(true));
    }

    @SuiteDoc(
        description = @Desc("My test suite containing various tests"),
        beforeTestSteps = {
            @Step(value = "Deploy uber operator across all namespaces, with custom configuration", expected = "Uber operator is deployed"),
            @Step(value = "Deploy management Pod for accessing all other Pods", expected = "Management Pod is deployed")
        },
        afterTestSteps = {
            @Step(value = "Delete management Pod", expected = "Management Pod is deleted"),
            @Step(value = "Delete uber operator", expected = "Uber operator is deleted")
        },
        labels = {
            @Label(value = "regression"),
            @Label(value = "clients")
        }
    )
    public static class DummyTest {

        @TestDoc(
            description = @Desc("Test checking that the application works as expected"),
            steps = {
                @Step(value = "Create object instance", expected = "Instance of an object is created"),
                @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
                @Step(value = "Clean up the test case", expected = "Everything is cleared")
            },
            labels = {
                @Label(value = "default")
            }
        )
        void testMethodOne() {

        }

        @TestDoc(
            description = @Desc("Test checking that the application works as expected. " +
                "This is just a little bit longer line, nothing else."),
            contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
            steps = {
                @Step(value = "Create object instance", expected = "Instance of an object is created"),
                @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
                @Step(value = "Clean up the test case", expected = "Everything is cleared"),
                @Step(value = "Do a magic cleanup check", expected = "Everything magically work")
            }
        )
        void testMethodTwo() {

        }

        @TestDoc(
            description = @Desc("Test checking that the application works as expected. " +
                "This is just a little bit longer line, nothing else."),
            contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io")
        )
        void testMethodThree() {

        }

        @TestDoc(
            description = @Desc("Test checking that the application works as expected. " +
                "This is just a little bit longer line, nothing else."),
            contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
            labels = {
                @Label(value = "default"),
                @Label(value = "regression"),
            }
        )
        void testMethodFour() {

        }
    }

    public static boolean compareFiles(String filePath1, String filePath2) throws IOException {
        byte[] file1Content = Files.readAllBytes(Paths.get(filePath1));
        byte[] file2Content = Files.readAllBytes(Paths.get(filePath2));

        return Arrays.equals(file1Content, file2Content);
    }
}
