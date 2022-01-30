package com.madvena.shorturl.exception;

public class UrlServiceException extends RuntimeException {
    public UrlServiceException(String message) {
        super(message);
    }

    public UrlServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
