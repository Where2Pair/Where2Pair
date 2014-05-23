package org.where2pair.main.venue

import static ratpack.groovy.test.TestHttpClients.testHttpClient

import groovy.json.JsonSlurper
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import spock.lang.Specification

class VenueFunctionalSpec extends Specification {
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

        assert response.statusCode == 200

        String savedVenueId = parsePropertyFromResponse('venueId', response.asString())

        Thread.sleep(500)

        resetRequest()
        get("venue/$savedVenueId")
        response.asString()
    }

    def cleanup() {
        aut.stop()
    }

    private String parsePropertyFromResponse(String property, String response) {
        new JsonSlurper().parseText(response)[property]
    }

    private static UUID randomName() {
        UUID.randomUUID()
    }
}

