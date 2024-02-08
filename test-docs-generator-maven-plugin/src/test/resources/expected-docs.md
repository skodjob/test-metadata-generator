# TestClass

**Description:** My test suite containing various tests

**Contact:** `Jakub Stejskal`

**Before tests execution steps:**

| Step | Action | Result |
| - | - | - |
| 1. | Deploy uber operator across all namespaces, with custom configuration | Uber operator is deployed |
| 2. | Deploy management Pod for accessing all other Pods | Management Pod is deployed |

**After tests execution steps:**

| Step | Action | Result |
| - | - | - |
| 1. | Delete management Pod | Management Pod is deleted |
| 2. | Delete uber operator | Uber operator is deleted |

**Use-cases:**

* `core`

**Tags:**

* `regression`
* `clients`

<hr style="border:1px solid">

## testMethodOne

**Description:** Test checking that the application works as expected

**Contact:** `Jakub Stejskal`

**Steps:**

| Step | Action | Result |
| - | - | - |
| 1. | Create object instance | Instance of an object is created |
| 2. | Do a magic trick | Magic trick is done with success |
| 3. | Clean up the test case | Everything is cleared |

**Use-cases:**

* `core`

**Tags:**

* `default`


## testMethodTwo

**Description:** Test checking that the application works as expected. This is just a little bit longer line, nothing else.

**Contact:** `Jakub Stejskal`

**Steps:**

| Step | Action | Result |
| - | - | - |
| 1. | Create object instance | Instance of an object is created |
| 2. | Do a magic trick | Magic trick is done with success |
| 3. | Clean up the test case | Everything is cleared |
| 4. | Do a magic cleanup check | Everything magically work |

**Use-cases:**

* `core`
* `core+`
* `core+++`


## testMethodThree

**Description:** Test checking that the application works as expected. This is just a little bit longer line, nothing else.

**Contact:** `Jakub Stejskal`


## testMethodFour

**Description:** Test checking that the application works as expected. This is just a little bit longer line, nothing else.

**Contact:** `Jakub Stejskal`

**Tags:**

* `default`
* `regression`
