/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import java.util.List;

/**
 * Class responsible for creating representation of Markdown table in text format, returned as String
 */
public class Table {

    /**
     * Constructor
     */
    private Table() {
        // constructor
    }

    /**
     * Creates the Markdown table in text format, containing specified headers and rows
     * @param headers list of headers that should be inside the table
     * @param rows list of rows added to the table
     * @return Markdown table in text format, returned as String
     */
    public static String createTable(List<String> headers, List<String> rows) {
        if (headers.isEmpty()) {
            return "";
        }
        StringBuilder table = new StringBuilder();

        table.append("|");
        headers.forEach(header -> table.append(" ").append(header).append(" |"));
        table.append("\n");
        table.append("|");
        headers.forEach(header -> table.append(" - |"));
        table.append("\n");

        rows.forEach(row -> table.append(row).append("\n"));

        return table.toString();
    }

    /**
     * Creates a single row for {@param content}
     * @param content of the row
     * @return table row in text format, returned as String
     */
    public static String createRow(String... content) {
        StringBuilder row = new StringBuilder();

        row.append("| ");
        for (String s : content) {

            row.append(s).append(" | ");
        }

        return row.substring(0, row.length() - 1);
    }
}
