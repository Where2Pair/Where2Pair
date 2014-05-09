package org.where2pair.infra.venue.web

import groovy.transform.Immutable

import static groovy.json.JsonOutput.toJson
import static org.where2pair.infra.venue.web.StatusCode.BAD_REQUEST
import static org.where2pair.infra.venue.web.StatusCode.NOT_FOUND
import static org.where2pair.infra.venue.web.StatusCode.OK

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