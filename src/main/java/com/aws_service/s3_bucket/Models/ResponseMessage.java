package com.aws_service.s3_bucket.Models;

import org.springframework.stereotype.Component;

import lombok.Data;

@Data
@Component
public class ResponseMessage {
    private boolean success;
    private String message;
}
