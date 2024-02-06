/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

/**
 * Class responsible for creating representation of Markdown headers in text format, returned as String
 */
public class Header {

    /**
     * Method for creating first level header containing {@param text}
     * @param text that should be in first level header
     * @return first level header with {@param text}
     */
    public static String firstLevelHeader(String text) {
        return "# " + text;
    }

    /**
     * Method for creating second level header containing {@param text}
     * @param text that should be in second level header
     * @return second level header with {@param text}
     */
    public static String secondLevelHeader(String text) {
        return "## " + text;
    }
}
