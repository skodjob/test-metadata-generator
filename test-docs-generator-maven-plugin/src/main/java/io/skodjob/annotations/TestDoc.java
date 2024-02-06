/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation collecting all information about the test-case.
 * Currently, we can specify following:
 *  - description
 *  - steps
 *  - usecases
 *
 * Example usage:
 * ```
 *     \@TestDoc(
 *         description = @Desc("Test checking that the application works as expected"),
 *         steps = {
 *             \@Step(value = "Create object instance", expected = "Instance of an object is created"),
 *             \@Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
 *             \@Step(value = "Clean up the test case", expected = "Everything is cleared")
 *         },
 *         usecases = {
 *             \@Usecase(id = "core")
 *         }
 *     )
 *     void testMyCode(ExtensionContext extensionContext) {
 *     }
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface TestDoc {

    /**
     * Description of particular test-case
     * @return description in {@link Desc}
     */
    Desc description();
    /**
     * Array of steps done in the test-case
     * @return array of steps in {@link Step}
     */
    Step[] steps();

    /**
     * Array of use-cases covered by test-case
     * @return array of use-cases in {@link Usecase}
     */
    Usecase[] usecases();

    /**
     * Interface for setting description inside the {@link TestDoc} annotation
     */
    @interface Desc {
        /**
         * Description of the test-case stored inside `value` field
         * @return description in String
         */
        String value();
    }

    /**
     * Interface for setting step inside the {@link TestDoc} annotation
     */
    @interface Step {
        /**
         * Step of the test-case stored inside `value` field
         * @return step in String
         */
        String value();

        /**
         * Expected outcome of the particular step in test-case
         * @return expected outcome in String
         */
        String expected();
    }

    /**
     * Interface for setting use-case inside the {@link TestDoc} annotation
     */
    @interface Usecase {
        /**
         * ID of the use-case, which is covered by the test-case
         * @return ID of the use-case in String
         */
        String id();
    }
}