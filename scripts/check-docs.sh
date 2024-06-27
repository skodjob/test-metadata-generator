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

# Base directories
CURRENT_DIR="$SCRIPT_DIR/../docs"
EXPECTED_DIR="$CURRENT_DIR/expected"
ACTUAL_DIR="$CURRENT_DIR/actual"

USECASE1="usecases/core+.md"
USECASE2="usecases/core.md"

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
compare_files "$ACTUAL_DIR/$OPTION_1/$USECASE1" "$EXPECTED_DIR/$OPTION_1/$USECASE1"
compare_files "$ACTUAL_DIR/$OPTION_1/$USECASE2" "$EXPECTED_DIR/$OPTION_1/$USECASE2"

compare_files "$ACTUAL_DIR/$OPTION_2/$OPTION_2_MD" "$EXPECTED_DIR/$OPTION_2/$OPTION_2_MD"
compare_files "$ACTUAL_DIR/$OPTION_2/$OPTION_2_FMF" "$EXPECTED_DIR/$OPTION_2/$OPTION_2_FMF"
compare_files "$ACTUAL_DIR/$OPTION_2/$USECASE1" "$EXPECTED_DIR/$OPTION_2/$USECASE1"
compare_files "$ACTUAL_DIR/$OPTION_2/$USECASE2" "$EXPECTED_DIR/$OPTION_2/$USECASE2"

compare_files "$ACTUAL_DIR/$OPTION_3/$OPTION_3_MD" "$EXPECTED_DIR/$OPTION_3/$OPTION_3_MD"
compare_files "$ACTUAL_DIR/$OPTION_3/$USECASE1" "$EXPECTED_DIR/$OPTION_3/$USECASE1"
compare_files "$ACTUAL_DIR/$OPTION_3/$USECASE2" "$EXPECTED_DIR/$OPTION_3/$USECASE2"

compare_files "$ACTUAL_DIR/$OPTION_4/$OPTION_4_MD" "$EXPECTED_DIR/$OPTION_4/$OPTION_4_MD"
compare_files "$ACTUAL_DIR/$OPTION_4/$USECASE1" "$EXPECTED_DIR/$OPTION_4/$USECASE1"
compare_files "$ACTUAL_DIR/$OPTION_4/$USECASE2" "$EXPECTED_DIR/$OPTION_4/$USECASE2"

