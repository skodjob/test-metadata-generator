#!/usr/bin/env bash

# Function to compare two files
compare_files() {
    file1=$1
    file2=$2

    if cmp -s "$file1" "$file2"; then
        echo "Files $file1 and $file2 are identical."
    else
        echo "Files $file1 and $file2 differ."
        diff "$file1" "$file2"
        exit 1
    fi
}

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

# Get module name from argument
MODULE_NAME="$1"
if [ -z "$MODULE_NAME" ]; then
    echo "Usage: $0 <module-name>"
    exit 1
fi

# Base directories
EXPECTED_DIR="$SCRIPT_DIR/../docs/expected"
ACTUAL_DIR="$SCRIPT_DIR/../$MODULE_NAME/target/test-docs/actual"

LABEL1="labels/default.md"
LABEL2="labels/regression.md"

OPTION_1="option1"
OPTION_1_MD="md/io/skodjob/DummyTest.md"
OPTION_1_FMF="fmf/io/skodjob/DummyTest.fmf"

OPTION_2="option2"
OPTION_2_MD="md/io.skodjob.DummyTest.md"
OPTION_2_FMF="fmf/io.skodjob.DummyTest.fmf"

OPTION_3="option3"
OPTION_3_MD="io.skodjob.DummyTest.md"

OPTION_4="option4"
OPTION_4_MD="io/skodjob/DummyTest.md"

compare_files "$ACTUAL_DIR/$OPTION_1/$OPTION_1_MD" "$EXPECTED_DIR/$OPTION_1/$OPTION_1_MD"
compare_files "$ACTUAL_DIR/$OPTION_1/$OPTION_1_FMF" "$EXPECTED_DIR/$OPTION_1/$OPTION_1_FMF"
compare_files "$ACTUAL_DIR/$OPTION_1/$LABEL1" "$EXPECTED_DIR/$OPTION_1/$LABEL1"
compare_files "$ACTUAL_DIR/$OPTION_1/$LABEL2" "$EXPECTED_DIR/$OPTION_1/$LABEL2"

compare_files "$ACTUAL_DIR/$OPTION_2/$OPTION_2_MD" "$EXPECTED_DIR/$OPTION_2/$OPTION_2_MD"
compare_files "$ACTUAL_DIR/$OPTION_2/$OPTION_2_FMF" "$EXPECTED_DIR/$OPTION_2/$OPTION_2_FMF"
compare_files "$ACTUAL_DIR/$OPTION_2/$LABEL1" "$EXPECTED_DIR/$OPTION_2/$LABEL1"
compare_files "$ACTUAL_DIR/$OPTION_2/$LABEL2" "$EXPECTED_DIR/$OPTION_2/$LABEL2"

compare_files "$ACTUAL_DIR/$OPTION_3/$OPTION_3_MD" "$EXPECTED_DIR/$OPTION_3/$OPTION_3_MD"
compare_files "$ACTUAL_DIR/$OPTION_3/$LABEL1" "$EXPECTED_DIR/$OPTION_3/$LABEL1"
compare_files "$ACTUAL_DIR/$OPTION_3/$LABEL2" "$EXPECTED_DIR/$OPTION_3/$LABEL2"

compare_files "$ACTUAL_DIR/$OPTION_4/$OPTION_4_MD" "$EXPECTED_DIR/$OPTION_4/$OPTION_4_MD"
compare_files "$ACTUAL_DIR/$OPTION_4/$LABEL1" "$EXPECTED_DIR/$OPTION_4/$LABEL1"
compare_files "$ACTUAL_DIR/$OPTION_4/$LABEL2" "$EXPECTED_DIR/$OPTION_4/$LABEL2"

