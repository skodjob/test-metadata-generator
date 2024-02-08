/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Interface for setting tag inside the {@link TestDoc} annotation
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Tag {
    /**
     * Tag of the test-case stored inside `value` field
     * @return tag in String
     */
    String value() default "";
}

