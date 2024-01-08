package com.aws_service.s3_bucket.S3Services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aws_service.s3_bucket.StaticInfos;
import com.aws_service.s3_bucket.Models.ResponseMessage;

import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;

@Service
public class S3Service {

    @Autowired
    private ResponseMessage responseMessage;

    public ResponseEntity<Object> listTheBucketsSerive() {
        S3Client client = StaticInfos.s3Client;
        ListBucketsRequest request = ListBucketsRequest.builder().build();
        ListBucketsResponse response = client.listBuckets(request);
        List<Bucket> buckets = response.buckets();

        if (!buckets.isEmpty()) {
            for (Bucket bucket : buckets) {
                System.out.println(bucket);
            }

            responseMessage.setSuccess(true);
            responseMessage.setMessage("Buckets fetched successfully!");
            return ResponseEntity.ok().body(responseMessage);

        }

        responseMessage.setSuccess(false);
        responseMessage.setMessage("No buckets");
        return ResponseEntity.ok().body(responseMessage);

    }
}
