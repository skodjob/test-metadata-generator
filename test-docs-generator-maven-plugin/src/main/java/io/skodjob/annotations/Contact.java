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
     * Contact name of the test-case stored inside `name` field
     * @return name in String
     */
    String name();

    /**
     * Contact email of the test-case stored inside `email` field
     * @return email in String
     */
    String email();
}
