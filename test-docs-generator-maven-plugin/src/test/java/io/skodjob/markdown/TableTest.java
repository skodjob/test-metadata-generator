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

public class TableTest {

    @Test
    void testCreateTable() {
        // Test data
        List<String> headers = Arrays.asList("Name", "Age", "City");
        List<String> rows = Arrays.asList("John Doe | 30 | New York", "Jane Smith | 25 | Los Angeles");

        // Expected table
        String expectedTable = "| Name | Age | City |\n" +
                "| - | - | - |\n" +
                "John Doe | 30 | New York\n" +
                "Jane Smith | 25 | Los Angeles\n";

        // Call the method
        String actualTable = Table.createTable(headers, rows);

        // Assertions
        assertThat(actualTable, is(expectedTable));
    }

    @Test
    void testCreateTableWithEmptyHeadersAndRows() {
        // Test data
        List<String> headers = Arrays.asList();
        List<String> rows = Arrays.asList();

        // Expected table
        String expectedTable = "";

        // Call the method
        String actualTable = Table.createTable(headers, rows);

        // Assertions
        assertThat(actualTable, is(expectedTable));
    }

    @Test
    void testCreateTableWithEmptyRows() {
        // Test data
        List<String> headers = Arrays.asList("Name", "Age");
        List<String> rows = Arrays.asList();

        // Expected table
        String expectedTable = "| Name | Age |\n" +
                "| - | - |\n";

        // Call the method
        String actualTable = Table.createTable(headers, rows);

        // Assertions
        assertThat(actualTable, is(expectedTable));
    }

    @Test
    void testCreateTableWithSingleHeaderAndRow() {
        // Test data
        List<String> headers = Arrays.asList("Name");
        List<String> rows = Arrays.asList("John Doe");

        // Expected table
        String expectedTable = "| Name |\n" +
                "| - |\n" +
                "John Doe\n";

        // Call the method
        String actualTable = Table.createTable(headers, rows);

        // Assertions
        assertThat(actualTable, is(expectedTable));
    }

    @Test
    void testCreateRow() {
        String item1 = "item1";
        String item2 = "item2";
        String item3 = "item3";
        String expectedRow = String.format("| %s | %s | %s |", item1, item2, item3);

        assertThat(Table.createRow(item1, item2, item3), is(expectedRow));
    }
}
