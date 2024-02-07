/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TextListTest {

    @Test
    void testCreateTextList() {
        // Test data
        List<String> objects = Arrays.asList("Apple", "Banana", "Orange");

        // Expected list
        String expectedList = "* Apple\n" +
                "* Banana\n" +
                "* Orange\n";

        // Call the method
        String actualList = TextList.createUnorderedList(objects);

        // Assertions
        assertThat(actualList, is(expectedList));
    }

    @Test
    void testCreateTextListWithEmptyList() {
        // Test data
        List<String> objects = List.of();

        // Expected list
        String expectedList = "";

        // Call the method
        String actualList = TextList.createUnorderedList(objects);

        // Assertions
        assertThat(actualList, is(expectedList));
    }

    @Test
    void testCreateTextListWithSingleObject() {
        // Test data
        List<String> objects = List.of("Apple");

        // Expected list
        String expectedList = "* Apple\n";

        // Call the method
        String actualList = TextList.createUnorderedList(objects);

        // Assertions
        assertThat(actualList, is(expectedList));
    }
}
