#!/bin/bash

release="v2.4.0"
split="true"

if [ $# -eq 2 ]
then
  release="v$1"
  split="$2"
fi

echo "Fetching release: ${release}"
commitSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/ref/tags/${release}" -q '.object.sha')
commitType=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/ref/tags/${release}" -q '.object.type')
# echo "Fetching ${commitSha} ${commitType}"
if [ "$commitType" = "tag" ] ; then
  #echo "Fetching tag ${commitSha}"
  commitSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/tags/${commitSha}" -q '.object.sha')
  #echo "Resolved tag to commit ${commitSha}"
fi
commitTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/commits/${commitSha}" -q '.tree.sha')
#echo "Fetching package ${commitTreeSha}"
packageTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${commitTreeSha}" -q '.tree[] | select(.path=="package") | .sha')
#echo "Fetching crds"
crdsTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${packageTreeSha}" -q '.tree[] | select(.path=="crds") | .sha')

# echo "Resolved commit SHA: ${commitSha}"
# echo "Resolved commit tree SHA: ${commitTreeSha}"
# echo "Resolved package tree SHA: ${packageTreeSha}"
# echo "Resolved crds tree SHA: ${crdsTreeSha}"

crds=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${crdsTreeSha}" --jq '[ .tree[].path]' | jq -r '.[]')
#crds=$(gh api --jq '.[].name' "/repos/crossplane-contrib/provider-upjet-azure/contents/package/crds?ref=${release}")


for crd in $crds;
do
  folder="./"
  if [ "$split" = "true" ] ; then
    folder="./$(echo $crd | cut -d'.' -f1 )/"
    # echo "${folder}"
    mkdir -p "${folder}"
  fi
  # output=$(echo $file | cut -d'_' -f2)
  gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane-contrib/provider-upjet-azure/contents/package/crds/${crd}?ref=${release}" > "${folder}${crd}"
  retVal=$?
  if [ $retVal -ne 0 ]; then
      echo "Failed to fetch ${crd}"
  fi

done

