package org.where2pair.common.venue

import groovy.transform.Immutable

import static groovy.json.JsonOutput.toJson

@Immutable
class JsonResponse {

    StatusCode statusCode
    String responseBody

    static JsonResponse validJsonResponse(def jsonData) {
        def json = toJson(jsonData)
        new JsonResponse(org.where2pair.common.venue.StatusCode.OK, json)
    }

    static JsonResponse badRequest(String errorMessage) {
        def json = toJson([error: errorMessage])
        new JsonResponse(org.where2pair.common.venue.StatusCode.BAD_REQUEST, json)
    }

    static JsonResponse resourceNotFound(String errorMessage) {
        def json = toJson([error: errorMessage])
        new JsonResponse(org.where2pair.common.venue.StatusCode.NOT_FOUND, json)
    }
}