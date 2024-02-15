/*
 * Copyright Skodjob authors.
 * License: Apache License 2.0 (see the file LICENSE or http://apache.org/licenses/LICENSE-2.0.html).
 */
package io.skodjob;

import io.skodjob.annotations.Contact;
import io.skodjob.annotations.Desc;
import io.skodjob.annotations.Step;
import io.skodjob.annotations.SuiteDoc;
import io.skodjob.annotations.TestDoc;
import io.skodjob.annotations.TestTag;
import io.skodjob.annotations.UseCase;
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
    useCases = {
        @UseCase(id = "core")
    },
    tags = {
        @TestTag(value = "regression"),
        @TestTag(value = "clients")
    }
)
public class DummyTest {

    @TestDoc(
        description = @Desc("Test checking that the application works as expected"),
        steps = {
            @Step(value = "Create object instance", expected = "Instance of an object is created"),
            @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
            @Step(value = "Clean up the test case", expected = "Everything is cleared")
        },
        useCases = {
            @UseCase(id = "core")
        },
        tags = {
            @TestTag(value = "default")
        }
    )
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
        },
        useCases = {
            @UseCase(id = "core"),
            @UseCase(id = "core+"),
            @UseCase(id = "core+++")
        }
    )
    void testMethodTwo() {

    }

    @TestDoc(
        description = @Desc("Test checking that the application works as expected. " +
                "This is just a little bit longer line, nothing else."),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io")
    )
    void testMethodThree() {

    }

    @TestDoc(
        description = @Desc("Test checking that the application works as expected. " +
                "This is just a little bit longer line, nothing else."),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
        tags = {
            @TestTag(value = "default"),
            @TestTag(value = "regression"),
        }
    )
    void testMethodFour() {

    }
}
