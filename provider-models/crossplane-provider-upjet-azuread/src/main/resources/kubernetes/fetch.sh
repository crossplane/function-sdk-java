#!/bin/bash

release="v1.5.0"

if [ $# -eq 1 ]
  then
    release="v$1"
fi

echo "Fetching release: ${release}"

crds=$(gh api --jq '.[].name' "/repos/crossplane-contrib/provider-upjet-azuread/contents/package/crds?ref=${release}")


for crd in $crds;
do
  # output=$(echo $file | cut -d'_' -f2)
  gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane-contrib/provider-upjet-azuread/contents/package/crds/${crd}?ref=${release}" > $crd
  retVal=$?
  if [ $retVal -ne 0 ]; then
      echo "Failed to fetch ${crd}"
  fi
done

