#!/bin/bash

release="release-1.14"
file="run_function.proto"
gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane/crossplane/contents/apis/apiextensions/fn/proto/v1beta1/${file}?ref=${release}" > $file

sed -i '/option go_package/a\option java_package = "io.crossplane.compositefunctions.protobuf";\noption java_multiple_files = true;' $file