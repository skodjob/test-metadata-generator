# Test Documentation Generator

Test Documentation Generator is a maven-plugin that delivers a set of annotation for your test code. 
You can annotate your test classes and test methods and provide details about test scenario.
Plugin itself then parse the data from the annotations and generates `Markdown` files for readable documentation and `fmf` format metadata for automated reporting of the test cases to external systems.

[![Build](https://github.com/skodjob/test-metadata-generator/actions/workflows/build.yaml/badge.svg?branch=main)](https://github.com/skodjob/test-metadata-generator/actions/workflows/build.yaml)
[![Publish-snapshot](https://github.com/skodjob/test-metadata-generator/actions/workflows/publish-snapshot.yaml/badge.svg?branch=main)](https://github.com/skodjob/test-metadata-generator/actions/workflows/publish-snapshot.yaml)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)
[![GitHub Release](https://img.shields.io/github/v/release/skodjob/test-frame)](https://github.com/skodjob/test-metadata-generator/releases)
[![Maven Central Version](https://img.shields.io/maven-central/v/io.skodjob/test-metadata-generator)](https://central.sonatype.com/search?q=io.skodjob.test-metadata-generator)

## Using the annotations

### Test class usage

To generate documentation of the test class, you can use the following annotations:

* `@SuiteDoc` - is the main annotation, which consists of all other annotation and should be used right above the method.
  It contains three fields - `description`, `steps`, and `usecases`, that are set using other annotations.
* `@Desc` - overall description of the test, it can contain anything.
* `@Contact` - contact info with fields `name` and `email`.
* `@Step` - particular step done in a test, contains two fields - `value` that contains the step information, `expected`
  is for the expected result of the step.
* `@Usecase` - one of the use-cases that the test is testing.
* `@TestTag` - specific tag for the test class.

Example of how the test can be annotated:
```java
    @SuiteDoc(
        description = @Desc("My test suite containing various tests"),
        contact = @Contact(name = "Jakub Stejskal", email = "ja@kub.io"),
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
    public static class DummyTest {
        // ...
    }
```

### Test method usage

To generate documentation of the test method, you can use the following annotations:

* `@TestDoc` - is the main annotation, which consists all other annotation and should be used right above the method.
  It contains three fields - `description`, `steps`, and `usecases`, that are set using other annotations.
* `@Desc` - overall description of the test, it can contain anything.
* `@Contact` - contact info with fields `name` and `email`.
* `@Step` - particular step done in a test, contains two fields - `value` that contains the step information, `expected`
  is for the expected result of the step.
* `@Usecase` - one of the use-cases that the test is testing.
* `@TestTag` - specific tag for the test class.

Example of how the test can be annotated:
```java
    @TestDoc(
        description = @Desc("Test checking that the application works as expected."),
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
        },
        tags = {
                @TestTag(value = "default"),
                @TestTag(value = "regression"),
        }
    )
    void testMyCode(ExtensionContext extensionContext) {
        // ...
    }
```

Syntax grammar (i.e., EBNF) of the test case
```plain
// Lexer rules
WS              : [ \t\r\n]+ -> skip;  // Ignore whitespace, tabs, carriage returns, and newlines
STRING          : '"' (~["\\])* '"';   // Matches quoted strings correctly handling all characters
NUMBER          : [0-9]+;              // For numbers, if needed

// Parser rules
testDocAnnotation : '@TestDoc' '(' testDocBody ')';
testDocBody       : testDocAttribute ( ',' testDocAttribute )* ;
testDocAttribute  : descriptionAttribute
                  | contactAttribute
                  | stepsAttribute
                  | useCasesAttribute
                  | tagsAttribute
                  ;

descriptionAttribute : 'description' '=' '@Desc' '(' STRING ')';
contactAttribute     : 'contact' '=' '@Contact' '(' contactBody ')';
contactBody          : 'name' '=' STRING ',' 'email' '=' STRING;
stepsAttribute       : 'steps' '=' '{' step ( ',' step )* '}';
step                 : '@Step' '(' 'value' '=' STRING ',' 'expected' '=' STRING ')';
useCasesAttribute    : 'useCases' '=' '{' useCase ( ',' useCase )* '}';
useCase              : '@UseCase' '(' 'id' '=' STRING ')';
tagsAttribute        : 'tags' '=' '{' testTag ( ',' testTag )* '}';
testTag              : '@TestTag' '(' 'value' '=' STRING ')';
```

## Generating the documentation

The generation is handled by maven-plugin that is available at [Maven central](https://central.sonatype.com/artifact/io.skodjob/test-docs-generator-maven-plugin/overview).
All released versions could be found there. 
For every commit we also publish `-SNAPSHOT` version to GitHub [packages](https://github.com/skodjob/test-metadata-generator/packages/2061096).

### Usage

To start using the plugin, you will need to add it to your pom file together with `maven-dependency-plugin` for building the dependencies of the test classes:

```xml
    <plugin>
        <artifactId>maven-dependency-plugin</artifactId>
        <executions>
            <execution>
                <phase>generate-sources</phase>
                <goals>
                    <goal>copy-dependencies</goal>
                </goals>
                <configuration>
                    <outputDirectory>${project.build.directory}/lib</outputDirectory>
                </configuration>
            </execution>
        </executions>
    </plugin>
    <plugin>
        <groupId>io.skodjob</groupId>
        <artifactId>test-docs-generator-maven-plugin</artifactId>
        <version>${generator.version}</version>
        <executions>
            <execution>
                <phase>post-integration-test</phase>
                <goals>
                    <goal>test-docs-generator</goal>
                </goals>
            </execution>
        </executions>
        <configuration>
            <testsPath>./dummy-module/src/test/java/io/</testsPath>
            <docsPath>./docs/</docsPath>
            <generateFmf>true</generateFmf>
        </configuration>
    </plugin>
```

`maven-dependency-plugin` is needed for proper loading the classes. 
Without it the plugin will return `NoClassDefFound` exception and will fail.

#### Accepted Parameters

Plugin works with the following parameters:
* `filePath` - path to the built classes, from where all the names of the tests are taken.
* `generatePath` - path to the place where the documentation should be generated.
* `generateFmf` - boolean value whether generator should generate also `fmf` metadata or just `Markdown`.


### Use SNAPSHOT version

To use `-SNAPSHOT` versions you have to have the plugin built on your local environment or use GitHub packages for a dependency resolution.

You can for example create the following `setting.xml`:
```xml
<settings>
    <servers>
        <server>
            <id>github</id>
            <username>x-access-token</username>
            <password>${env.GITHUB_TOKEN}</password>
        </server>
    </servers>
    <pluginGroups>
        <pluginGroup>io.skodjob</pluginGroup>
    </pluginGroups>
</settings>
```

And specify GitHub in your pom:
```xml
    <repositories>
        <repository>
            <id>github</id>
            <name>GitHub Apache Maven Packages</name>
            <url>https://maven.pkg.github.com/skodjob/test-metadata-generator</url>
        </repository>
    </repositories>
```
