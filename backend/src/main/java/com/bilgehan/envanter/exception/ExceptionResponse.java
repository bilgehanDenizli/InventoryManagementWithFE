package com.bilgehan.envanter.exception;


import java.util.Date;

public class ExceptionResponse {
    private Date timestamp;
    private String uri;
    private String message;


    public ExceptionResponse(Date timestamp, String message, String uri) {
        this.timestamp = timestamp;
        this.uri = uri;
        this.message = message;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
