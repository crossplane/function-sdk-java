#!/bin/bash

release="v1.20.4"

apiextensions=$(gh api --jq '.[].name' "/repos/crossplane/crossplane/contents/cluster/crds?ref=${release}" | grep apiextensions)


for file in $apiextensions;
do
  # output=$(echo $file | cut -d'_' -f2)
  gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane/crossplane/contents/cluster/crds/${file}?ref=${release}" > $file
done
