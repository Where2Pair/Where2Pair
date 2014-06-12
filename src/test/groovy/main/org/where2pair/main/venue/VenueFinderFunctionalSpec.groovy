package org.where2pair.main.venue

import static ratpack.groovy.test.TestHttpClients.testHttpClient

import groovy.json.JsonSlurper
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

        def initialResponseAsString = response.asString()
        def initialResponseStatusCode = response.statusCode
        assert initialResponseStatusCode == 200, "Response code: $initialResponseStatusCode, body: $initialResponseAsString"
        def savedVenueId = parsePropertyFromResponse('venueId', initialResponseAsString)

        Thread.sleep(500)
        resetRequest()
        get("venue/$savedVenueId")

        def secondResponseAsString = response.asString()
        def secondResponseStatusCode = response.statusCode
        assert secondResponseStatusCode == 200, "Response code: $secondResponseStatusCode, body: $secondResponseAsString"
        secondResponseAsString
    }

    def cleanup() {
        aut.stop()
    }

    private static String parsePropertyFromResponse(String property, String response) {
        new JsonSlurper().parseText(response)[property]
    }

    private static UUID randomName() {
        UUID.randomUUID()
    }
}

