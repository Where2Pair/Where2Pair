package org.where2pair.infra.venue.web

import groovy.transform.Immutable

@Immutable
class ServerResponse {

    StatusCode statusCode
    String responseBody

}