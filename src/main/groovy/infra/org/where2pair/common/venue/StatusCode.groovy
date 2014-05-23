package org.where2pair.common.venue

enum StatusCode {

    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404)

    private final int statusCode

    private StatusCode(int statusCode) {
        this.statusCode = statusCode
    }

    int getValue() {
        statusCode
    }
}

