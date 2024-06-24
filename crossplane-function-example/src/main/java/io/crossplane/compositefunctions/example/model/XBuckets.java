package io.crossplane.compositefunctions.example.model;

import io.fabric8.kubernetes.client.CustomResource;
import io.fabric8.kubernetes.model.annotation.Group;
import io.fabric8.kubernetes.model.annotation.Plural;
import io.fabric8.kubernetes.model.annotation.Singular;
import io.fabric8.kubernetes.model.annotation.Version;

@Version(value = "v1", storage = true, served = true)
@Group("example.crossplane.io")
@Singular("XBuckets")
@Plural("XBucketsList")
public class XBuckets extends CustomResource<XBucketsSpec, Void> {



}
