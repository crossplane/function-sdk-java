#!/bin/bash

javaRelease="2.3.0"
release="v${javaRelease}"
split="false"

if [ $# -eq 2 ]
then
  javaRelease="$1"
  release="v$1"
  split="$2"
fi

echo "Fetching release: ${release}"

commitSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/ref/tags/${release}" -q '.object.sha')
commitTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/commits/${commitSha}" -q '.tree.sha')
packageTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${commitTreeSha}" -q '.tree[] | select(.path=="package") | .sha')
crdsTreeSha=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${packageTreeSha}" -q '.tree[] | select(.path=="crds") | .sha')

# echo "Resolved commit SHA: ${commitSha}"
# echo "Resolved commit tree SHA: ${commitTreeSha}"
# echo "Resolved package tree SHA: ${packageTreeSha}"
# echo "Resolved crds tree SHA: ${crdsTreeSha}"

crds=$(gh api "repos/crossplane-contrib/provider-upjet-azure/git/trees/${crdsTreeSha}" --jq '[ .tree[].path]' | jq -r '.[]')
#crds=$(gh api --jq '.[].name' "/repos/crossplane-contrib/provider-upjet-azure/contents/package/crds?ref=${release}")

groups=$(echo -e "$crds" | cut -d'.' -f1 | sort | uniq)

for crd in $crds;
do
  folder="./src/main/resources/kubernetes/"
  if [ "$split" = "true" ] ; then
    folder="${folder}$(echo $crd | cut -d'.' -f1 )/"
    mkdir -p "${folder}"
  fi
  # output=$(echo $file | cut -d'_' -f2)
  gh api   -H "Accept: application/vnd.github.raw+json"  "/repos/crossplane-contrib/provider-upjet-azure/contents/package/crds/${crd}?ref=${release}" > "${folder}${crd}"
  retVal=$?
  if [ $retVal -ne 0 ]; then
      echo "Failed to fetch ${crd}"
  fi

done

mvn versions:set-property -Dproperty=modelrevision "-DnewVersion=${javaRelease}" -DgenerateBackupPoms=false
mvn -B deploy --file pom.xml -Pdeploy -Dmodelrevision="${javaRelease}"
if [ "$split" = "true" ] ; then
  for group in $groups; do
    mvn versions:set-property -Dproperty=modelDirectory -DnewVersion="/${group}" -DgenerateBackupPoms=false
    mvn versions:set-property -Dproperty=modelGroup -DnewVersion="${group}" -DgenerateBackupPoms=false
    mvn -B clean deploy --file pom.xml -Pdeploy -Dmodelrevision="${javaRelease}" -DmodelDirectory="/${group}" -DmodelGroup="${group}"
  done
fi







