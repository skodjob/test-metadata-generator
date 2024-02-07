/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.TestDoc;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class DocGeneratorTest {
    @Test
    void testCreateTableOfSteps() throws IOException {
        String expectedFilePath = DocGeneratorTest.class.getClassLoader().getResource("expected-docs.md").getPath();
        String generatedFilePath = "target/io/test.md";
        DocGenerator.generate(TestClass.class, generatedFilePath);

        assertThat(compareFiles(expectedFilePath, generatedFilePath), is(true));
    }

    public static class TestClass {

        @TestDoc(
            description = @TestDoc.Desc("Test checking that the application works as expected"),
            steps = {
                @TestDoc.Step(value = "Create object instance", expected = "Instance of an object is created"),
                @TestDoc.Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
                @TestDoc.Step(value = "Clean up the test case", expected = "Everything is cleared")
            },
            usecases = {
                @TestDoc.Usecase(id = "core")
            }
        )
        void testMethodOne() {

        }

        @TestDoc(
            description = @TestDoc.Desc("Test checking that the application works as expected. " +
                    "This is just a little bit longer line, nothing else."),
            steps = {
                @TestDoc.Step(value = "Create object instance", expected = "Instance of an object is created"),
                @TestDoc.Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
                @TestDoc.Step(value = "Clean up the test case", expected = "Everything is cleared"),
                @TestDoc.Step(value = "Do a magic cleanup check", expected = "Everything magically work")
            },
            usecases = {
                @TestDoc.Usecase(id = "core"),
                @TestDoc.Usecase(id = "core+"),
                @TestDoc.Usecase(id = "core+++")
            }
        )
        void testMethodTwo() {

        }

        @TestDoc(
            description = @TestDoc.Desc("Test checking that the application works as expected. " +
                    "This is just a little bit longer line, nothing else."),
            steps = {
            },
            usecases = {
            }
        )
        void testMethodThree() {

        }
    }

    public static boolean compareFiles(String filePath1, String filePath2) throws IOException {
        byte[] file1Content = Files.readAllBytes(Paths.get(filePath1));
        byte[] file2Content = Files.readAllBytes(Paths.get(filePath2));

        return Arrays.equals(file1Content, file2Content);
    }
}
