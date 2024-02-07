/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.markdown;

import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class HeaderTest {

    @Test
    void testFirstLevelHeader() {
        String text = "Something";
        String expectedHeader = "# Something";

        assertThat(expectedHeader, is(Header.firstLevelHeader(text)));
    }

    @Test
    void testSecondLevelHeader() {
        String text = "Something";
        String expectedHeader = "## Something";

        assertThat(expectedHeader, is(Header.secondLevelHeader(text)));
    }
}
