/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class FmfGeneratorTest {
    @Test
    void testCreateTableOfSteps() throws IOException {
        String expectedFilePath = FmfGeneratorTest.class.getClassLoader().getResource("expected-docs.fmf").getPath();
        String generatedFilePath = "target/io/test.fmf";
        FmfGenerator.generate(MdGeneratorTest.TestClass.class, generatedFilePath);

        assertThat(MdGeneratorTest.compareFiles(expectedFilePath, generatedFilePath), is(true));
    }
}
