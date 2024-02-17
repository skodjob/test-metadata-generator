#!/usr/bin/env bash

DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" >/dev/null 2>&1 && pwd)"

fmf_output="${DIR}/../docs/fmf/io/skodjob/DummyTest.fmf"
fmf_expected="${DIR}/../test-docs-generator-maven-plugin/src/test/resources/expected-docs.fmf"

md_output="${DIR}/../docs/md/io/skodjob/DummyTest.md"
md_expected="${DIR}/../test-docs-generator-maven-plugin/src/test/resources/expected-docs.md"

if cmp -s "$fmf_output" "$fmf_expected"; then
    echo "The files \"$fmf_output\" and \"$fmf_expected\" are the same."
else
    echo "The files \"$fmf_output\" and \"$fmf_expected\" are different."
    diff "$fmf_output" "$fmf_expected"
    exit 1
fi

if cmp -s "$md_output" "$md_expected"; then
    echo "The files \"$md_output\" and \"$md_expected\" are the same."
else
    echo "The files \"$md_output\" and \"$md_expected\" are different."
    diff "$md_output" "$md_expected"
    exit 1
fi
