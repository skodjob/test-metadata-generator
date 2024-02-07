/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

/**
 * Class responsible for creating representation of Markdown lines in text format, returned as String
 */
public class Line {

    /**
     * Creates horizontal line
     * @return horizontal line in String
     */
    public static String horizontalLine() {
        return "<hr style=\"border:1px solid\">";
    }
}
