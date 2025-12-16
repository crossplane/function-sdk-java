#!/bin/bash

release="v2.0.6"
file="run_function.proto"
gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane/crossplane/contents/proto/fn/v1/${file}?ref=${release}" > $file

sed -i '/option go_package/a\option java_package = "io.crossplane.compositefunctions.protobuf.v1";\noption java_multiple_files = true;' $file
