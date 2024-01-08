package com.aws_service.s3_bucket.Models;

import lombok.Data;

@Data
public class BucketNameAndKey {
    private String bucketName;
    private String key;
}
