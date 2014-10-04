package org.where2pair.main.venue

import groovyx.net.http.RESTClient
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import spock.lang.Specification

class VenueFinderFunctionalSpec extends Specification {
    static final String VENUE_NAME = randomName()

    def aut = new LocalScriptApplicationUnderTest()
    def restClient = new RESTClient(aut.address)

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
        retrievedVenue.name == VENUE_NAME
    }

    private Map storeAndRetrieve(venueJson) {
        def response = restClient.post (
            path: 'venue',
            body: venueJson,
            requestContentType: 'application/json'
        )

        ensureContentTypeAndStatusCode(response)
        def savedVenueId = response.data.venueId

        Thread.sleep(500)
        def secondResponse = restClient.get(path: "venue/$savedVenueId")

        ensureContentTypeAndStatusCode(secondResponse)
        secondResponse.data
    }

    private static void ensureContentTypeAndStatusCode(response) {
        assert response.status == 200 && response.contentType == 'application/json', "Response code: $response.status, body: $response.data.toString(), contentType: $response.contentType"
    }

    private static UUID randomName() {
        UUID.randomUUID()
    }

    def cleanup() {
        aut.stop()
    }
}

