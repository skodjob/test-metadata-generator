/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import java.util.List;

/**
 * Class responsible for creating representation of Markdown lists in text format, returned as String
 */
public class TextList {

    /**
     * Creates the Markdown unordered list in text format, containing all from the {@param objects}
     * @param objects list of objects that should be inside the unordered list
     * @return Markdown unordered list in text format, returned as String
     */
    public static String createUnorderedList(List<String> objects) {
        StringBuilder builder = new StringBuilder();
        objects.forEach(object ->
            builder.append("* " + object + "\n")
        );

        return builder.toString();
    }
}
