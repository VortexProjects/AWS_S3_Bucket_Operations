package com.aws_service.s3_bucket;


import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

public class StaticInfos {
    public final static Region region = Region.AP_NORTHEAST_3;
    public final static S3Client s3Client = S3Client.builder().region(region).build();
}
