package org.where2pair.common.venue

import static groovy.json.JsonOutput.toJson
import static org.where2pair.common.venue.StatusCode.BAD_REQUEST
import static org.where2pair.common.venue.StatusCode.NOT_FOUND
import static org.where2pair.common.venue.StatusCode.OK

import groovy.transform.Immutable

@Immutable
class JsonResponse {

    StatusCode statusCode
    String responseBody

    static JsonResponse validJsonResponse(def jsonData) {
        def json = toJson(jsonData)
        new JsonResponse(OK, json)
    }

    static JsonResponse badRequest(String errorMessage) {
        def json = toJson([error: errorMessage])
        new JsonResponse(BAD_REQUEST, json)
    }

    static JsonResponse resourceNotFound(String errorMessage) {
        def json = toJson([error: errorMessage])
        new JsonResponse(NOT_FOUND, json)
    }
}
