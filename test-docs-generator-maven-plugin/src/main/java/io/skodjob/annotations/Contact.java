/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface for setting contact inside the {@link TestDoc} annotation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Contact {
    /**
     * Contact name of the test-case stored inside `value` field
     * @return contact in String
     */
    String value();
}
