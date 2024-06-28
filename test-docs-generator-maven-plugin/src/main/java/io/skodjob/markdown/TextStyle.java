/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

/**
 * Class responsible for applying Markdown text styles to specified text, returned as String
 */
public class TextStyle {

    /**
     * Constructor
     */
    private TextStyle() {
        // constructor
    }

    /**
     * Method for adding bold style to specified {@param text}
     *
     * @param text that should be in bold style
     * @return bold stylization for {@param text}
     */
    public static String boldText(String text) {
        return "**" + text + "**";
    }
}
