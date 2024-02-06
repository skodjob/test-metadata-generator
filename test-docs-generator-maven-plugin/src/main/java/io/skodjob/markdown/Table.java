/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import java.util.List;

public class Table {

    public static String createTable(List<String> headers, List<String> rows) {
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

    public static String createRow(String... content) {
        StringBuilder row = new StringBuilder();

        for (int i = 0; i < content.length; i++) {
            row.append("| ").append(content[i]);
        }

        row.append(" |");

        return row.toString();
    }
}
