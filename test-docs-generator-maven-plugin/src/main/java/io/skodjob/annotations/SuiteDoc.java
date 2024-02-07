/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Annotation collecting all information about the test-suite.
 * Currently, we can specify following:
 *  - description
 *  - beforeTestSteps
 *  - afterTestSteps
 *  - useCases
 *
 * Example usage:
 * ```
 *     \@SuiteDoc(
 *         description = @Desc("My test suite containing various tests"),
 *         beforeTestSteps = {
 *             \@Step(value = "Deploy uber operator across all namespaces, with custom configuration", expected = "Uber operator is deployed"),
 *             \@Step(value = "Deploy management Pod for accessing all other Pods", expected = "Management Pod is deployed")
 *         },
 *         afterTestSteps = {
 *             \@Step(value = "Delete management Pod", expected = "Management Pod is deleted"),
 *             \@Step(value = "Delete uber operator", expected = "Uber operator is deleted")
 *         },
 *         useCases = {
 *             \@UseCase(id = "core")
 *         }
 *     )
 *     public static class TestClass {
 * ```
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SuiteDoc {

    /**
     * Description of particular test-suite
     * @return description in {@link Desc}
     */
    Desc description();

    /**
     * Array of steps done before tests execution
     * @return array of steps in {@link Step}
     */
    Step[] beforeTestSteps() default {};

    /**
     * Array of steps done after tests execution
     * @return array of steps in {@link Step}
     */
    Step[] afterTestSteps() default {};

    /**
     * Array of use-cases covered by test-suite
     * @return array of use-cases in {@link UseCase}
     */
    UseCase[] useCases() default {};
}