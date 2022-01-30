package com.madvena.shorturl.exception;

public class Base62Exception extends RuntimeException {
    public Base62Exception(String message) {
        super(message);
    }
}
