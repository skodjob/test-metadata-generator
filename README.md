# Test documentation generator

TODO

## Annotating the tests

To generate documentation of the test, you can use the following annotations:

* `@TestDoc` - is the main annotation, which consists all other annotation and should be used right above the method.
  It contains three fields - `description`, `steps`, and `usecases`, that are set using other annotations.
* `@Desc` - overall description of the test, it can contain anything
* `@Step` - particular step done in a test, contains two fields - `value` that contains the step information, `expected`
  is for the expected result of the step.
* `@Usecase` - one of the use-cases that the test is testing.

Example of how the test can be annotated:
```java
    @TestDoc(
        description = @Desc("Test checking that the application works as expected"),
        steps = {
            @Step(value = "Create object instance", expected = "Instance of an object is created"),
            @Step(value = "Do a magic trick", expected = "Magic trick is done with success"),
            @Step(value = "Clean up the test case", expected = "Everything is cleared")
        },
        usecases = {
            @Usecase(id = "core")
        }
    )
    void testMyCode(ExtensionContext extensionContext) {
    }
```

## Generating the documentation

The `DocGenerator` needs to have all dependencies built before it is executed.
That can be done using:
```bash
mvn clean install -DskipTests
```
from the root directory of the project.

After that, the generator can be executed using the following command:
```bash
java -classpath ../systemtest/target/lib/\*:../systemtest/target/test-classes:../systemtest/target/systemtest-<RELEASE-VERSION>.jar \
io.strimzi.systemtestdoc.DocGenerator --filePath ../systemtest/src/test/java/io/strimzi/systemtest --generatePath ./documentation/
```

The generator accepts two arguments:

* `--filePath` - path to the built classes, from where all the names of the tests are taken
* `--generatePath` - path to the place where the documentation should be generated

Or you can use the `make` command from the repository root:
```bash
make generate-docs -C systemtest-doc
```
To remove the generated documentation, you can use:
```bash
make clean-docs -C systemtest-doc
```