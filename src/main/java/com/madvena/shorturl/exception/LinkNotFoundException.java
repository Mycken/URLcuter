package com.madvena.shorturl.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value = HttpStatus.NOT_FOUND)
public class LinkNotFoundException extends UrlServiceException {
    public LinkNotFoundException(String link) {
        super(String.format("link '%s' not found", link));
    }
}
