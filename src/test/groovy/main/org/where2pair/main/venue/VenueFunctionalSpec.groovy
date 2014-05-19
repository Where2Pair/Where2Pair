package org.where2pair.main.venue

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import ratpack.groovy.launch.GroovyScriptFileHandlerFactory
import ratpack.groovy.test.LocalScriptApplicationUnderTest
import ratpack.groovy.test.TestHttpClient
import spock.lang.Specification

import static ratpack.groovy.launch.GroovyScriptFileHandlerFactory.SCRIPT_PROPERTY_NAME
import static ratpack.groovy.test.TestHttpClients.testHttpClient

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
        def where2pair =  new RESTClient(aut.address)//new RESTClient('http://localhost:5050/')
        //where2pair.auth.basic('testUser', 'password')

        def putResponse = where2pair.post(path: 'venue', body: venueJson, requestContentType: ContentType.JSON)
        assert putResponse.status == 200
        String savedVenueId = parsePropertyFromResponse('venueId', putResponse.data.text)

        Thread.sleep(500)

        def getResponse = where2pair.get(path: "venue/$savedVenueId", requestContentType: ContentType.URLENC)
        getResponse.data.text
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
