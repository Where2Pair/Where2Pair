package org.where2pair.main.venue

import static ratpack.groovy.test.TestHttpClients.testHttpClient

import com.jayway.restassured.response.Response
import groovy.json.JsonSlurper
import groovy.transform.TupleConstructor
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import spock.lang.Specification

class VenueFinderFunctionalSpec extends Specification {
    static final String VENUE_NAME = randomName()

    LocalScriptApplicationUnderTest aut = new LocalScriptApplicationUnderTest()
    @Delegate TestHttpClient client = testHttpClient(aut)

    def 'store and retrieve venue though the REST api'() {
        given:
        Map venueJson = [name: VENUE_NAME,
                address: [
                    addressLine1: 'addressLine1',
                    city: 'city',
                    postcode: 'postcode'
                ],
                location: [
                        latitude: 1.0,
                        longitude: 0.5],
                openHours: ['monday': [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 0]]]]

        when:
        def retrievedVenue = storeAndRetrieve(venueJson)

        then:
        parsePropertyFromResponse('name', retrievedVenue) == VENUE_NAME
    }

    private String storeAndRetrieve(venueJson) {
        request.contentType('application/json').body(venueJson)
        post('venue')

        def responseParams = extractResponseParams(response)
        ensureContentTypeAndStatusCode(responseParams)
        def savedVenueId = parsePropertyFromResponse('venueId', responseParams.body)

        Thread.sleep(500)
        resetRequest()
        get("venue/$savedVenueId")

        def secondResponseParams = extractResponseParams(response)
        ensureContentTypeAndStatusCode(secondResponseParams)
        secondResponseParams.body
    }

    private void ensureContentTypeAndStatusCode(ResponseParams responseParams) {
        assert responseParams.statusCode == 200 && responseParams.contentType == 'application/json', "Response code: $responseParams.statusCode, body: $responseParams.body, contentType: $responseParams.contentType"
    }

    def cleanup() {
        aut.stop()
    }

    private static ResponseParams extractResponseParams(Response response) {
        new ResponseParams(response.asString(), response.contentType, response.statusCode)
    }

    private static String parsePropertyFromResponse(String property, String response) {
        new JsonSlurper().parseText(response)[property]
    }

    private static UUID randomName() {
        UUID.randomUUID()
    }

    @TupleConstructor
    private static class ResponseParams {
        String body
        String contentType
        int statusCode
    }
}

