package com.aws_service.s3_bucket.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class BucketNameKeyPath {
    private String bucketName;
    private String key;
    private String path;
}
