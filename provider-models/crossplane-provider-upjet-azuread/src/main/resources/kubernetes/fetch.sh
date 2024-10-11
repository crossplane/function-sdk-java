#!/bin/bash

release="v1.5.0"

crds=$(gh api --jq '.[].name' "/repos/crossplane-contrib/provider-upjet-azuread/contents/package/crds/?ref=${release}")


for crd in $crds;
do
  # output=$(echo $file | cut -d'_' -f2)
  gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane-contrib/provider-upjet-azuread/contents/package/crds/${crd}?ref=${release}" > $crd
done
