package com.reactivespring.exception;

public class MoviesInfoClientException extends RuntimeException {

    private String message;
    private Integer statusCode;

    public MoviesInfoClientException(String message, Integer statusCode) {
        super(message);
        this.message = message;
        this.statusCode = statusCode;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public Integer getStatusCode() {
        return statusCode;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setStatusCode(Integer statusCode) {
        this.statusCode = statusCode;
    }
}

