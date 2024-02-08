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
 *  - useCases
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
 *         useCases = {
 *             \@UseCase(id = "core")
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
     * Contact name of particular test-case
     * @return contact name in {@link Contact}
     */
    Contact contact() default @Contact(name = "", email = "");

    /**
     * Array of steps done in the test-case
     * @return array of steps in {@link Step}
     */
    Step[] steps() default {};

    /**
     * Array of use-cases covered by test-case
     * @return array of use-cases in {@link UseCase}
     */
    UseCase[] useCases() default {};

    /**
     * Array of tags describing the test-case
     * @return array of tags in {@link TestTag}
     */
    TestTag[] tags() default {};
}
