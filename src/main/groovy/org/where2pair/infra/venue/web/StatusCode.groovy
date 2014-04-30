package org.where2pair.infra.venue.web


public enum StatusCode {

    OK(200),
    BAD_REQUEST(400),
    NOT_FOUND(404)

    private final int statusCode

    private StatusCode(int statusCode) {
        this.statusCode = statusCode
    }
}