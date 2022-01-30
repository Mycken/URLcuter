package com.madvena.shorturl.model.dto;

import lombok.Value;

@Value
public class ErrorResponse {
    private String timestamp;
    private String path;
    private String message;
    private int status;
}
