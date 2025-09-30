/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.Contact;
import io.skodjob.annotations.Desc;
import io.skodjob.annotations.Label;
import io.skodjob.annotations.Step;
import io.skodjob.annotations.SuiteDoc;
import io.skodjob.annotations.TestDoc;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.Extension;

@SuiteDoc(
    description = @Desc("My test suite containing various tests"),
    beforeTestSteps = {
        @Step(value = "Deploy uber operator across all namespaces, with custom configuration", expected = "Uber operator is deployed"),
        @Step(value = "Deploy management Pod for accessing all other Pods", expected = "Management Pod is deployed")
    },
    afterTestSteps = {
        @Step(value = "Delete management Pod", expected = "Management Pod is deleted"),
        @Step(value = "Delete uber operator", expected = "Uber operator is deleted")
    },
    labels = {
        @Label(value = "regression"),
        @Label(value = "clients")
    }
)
// Implements JUnit extension to force requirement that JUnit API is properly on the class-path when
// this class is loaded.
class DummyTest implements Extension {

    @TestDoc(
        description = @Desc("Test checking that the application works as expected"),
        steps = {
            @Step(value = "Create object instance", expected = "Instance of an object is created"),
            @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
            @Step(value = "Clean up the test case", expected = "Everything is cleared")
        },
        labels = {
            @Label(value = "default")
        }
    )
    @Test
    void testMethodOne() {

    }

    @TestDoc(
        description = @Desc("Test checking that the application works as expected. " +
            "This is just a little bit longer line, nothing else."),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
        steps = {
            @Step(value = "Create object instance", expected = "Instance of an object is created"),
            @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
            @Step(value = "Clean up the test case", expected = "Everything is cleared"),
            @Step(value = "Do a magic cleanup check", expected = "Everything magically work")
        }
    )
    @Test
    void testMethodTwo() {

    }

    @TestDoc(
        description = @Desc("Test checking that the application works as expected. " +
            "This is just a little bit longer line, nothing else."),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io")
    )
    @Test
    void testMethodThree() {

    }

    @TestDoc(
        description = @Desc("Test checking that the application works as expected. " +
            "This is just a little bit longer line, nothing else."),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
        labels = {
            @Label(value = "default"),
            @Label(value = "regression"),
        }
    )
    @Test
    void testMethodFour() {

    }
}
