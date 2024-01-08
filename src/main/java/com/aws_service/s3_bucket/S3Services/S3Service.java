package com.aws_service.s3_bucket.S3Services;

import java.io.File;
import java.time.Duration;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.aws_service.s3_bucket.StaticInfos;
import com.aws_service.s3_bucket.Models.BucketNameAndKey;
import com.aws_service.s3_bucket.Models.BucketNameKeyPath;
import com.aws_service.s3_bucket.Models.ResponseMessage;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsRequest;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;
import software.amazon.awssdk.services.s3.presigner.model.PresignedGetObjectRequest;

@Service
public class S3Service {

    @Autowired
    private ResponseMessage responseMessage;

    @Autowired
    private BucketNameAndKey bucketNameAndKey;

    public boolean checkObjectInBucket(String bucketName, String key) {
        S3Client s3Client = StaticInfos.s3Client;

        try {
            HeadObjectRequest headObjectRequest = HeadObjectRequest.builder()
                    .bucket(bucketName)
                    .key(key)
                    .build();

            s3Client.headObject(headObjectRequest);

            return true;
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return false;
        }
    }

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

    public ResponseEntity<Object> preSignedURLService(BucketNameAndKey bucketNameAndKey) {
        S3Presigner s3Client = S3Presigner.builder().region(StaticInfos.region).build();

        try {
            if (checkObjectInBucket(bucketNameAndKey.getBucketName(), bucketNameAndKey.getKey())) {
                GetObjectRequest request = GetObjectRequest.builder()
                        .bucket(bucketNameAndKey.getBucketName())
                        .key(bucketNameAndKey.getKey())
                        .build();

                GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                        .signatureDuration(Duration.ofSeconds(30))
                        .getObjectRequest(request)
                        .build();

                PresignedGetObjectRequest presignedGetObjectRequest = s3Client.presignGetObject(presignRequest);

                responseMessage.setSuccess(true);
                responseMessage.setMessage(presignedGetObjectRequest.url().toString());

                return ResponseEntity.ok().body(responseMessage);
            } else {
                responseMessage.setSuccess(false);
                responseMessage.setMessage("Object '" + bucketNameAndKey.getKey() + "' does not exists!");
                return ResponseEntity.ok().body(responseMessage);
            }
        } catch (Exception e) {
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Internal Server Error");
            return ResponseEntity.badRequest().body(responseMessage);
        }
    }

    public ResponseEntity<Object> putObjectService(BucketNameKeyPath bucketNameKeyPath) {
        Region region = Region.AP_NORTHEAST_2;
        S3Client client = S3Client.builder().region(region).build();

        try {
            PutObjectRequest putOb = PutObjectRequest.builder()
                    .bucket(bucketNameKeyPath.getBucketName())
                    .key(bucketNameKeyPath.getKey())
                    .build();

            client.putObject(putOb, RequestBody.fromFile(new File(bucketNameKeyPath.getPath())));

            bucketNameAndKey.setBucketName(bucketNameKeyPath.getBucketName());
            bucketNameAndKey.setKey(bucketNameKeyPath.getKey());

            responseMessage.setSuccess(true);
            responseMessage.setMessage("Object " + bucketNameKeyPath.getKey() + " insertion success"+ preSignedURLService(bucketNameAndKey));

            return ResponseEntity.ok().body(responseMessage);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            responseMessage.setSuccess(false);
            responseMessage.setMessage("Object " + bucketNameKeyPath.getKey() + " insertion falied");

            return ResponseEntity.badRequest().body(responseMessage);
        }
    }
}
