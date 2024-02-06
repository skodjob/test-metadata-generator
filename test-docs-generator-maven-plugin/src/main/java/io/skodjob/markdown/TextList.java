/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import java.util.List;

public class TextList {

    public static String createOrderedList(List<String> objects) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < objects.size(); i++) {
            builder.append(i + 1 + ". " + objects.get(i) + "\n");
        }

        return builder.toString();
    }

    public static String createUnorderedList(List<String> objects) {
        StringBuilder builder = new StringBuilder();
        objects.forEach(object ->
            builder.append("* " + object + "\n")
        );

        return builder.toString();
    }
}
