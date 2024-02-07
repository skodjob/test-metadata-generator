/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class LineTest {

    @Test
    void testHorizontalLine() {
        String expectedLine = "<hr style=\"border:1px solid\">";

        assertThat(expectedLine, is(Line.horizontalLine()));
    }
}
