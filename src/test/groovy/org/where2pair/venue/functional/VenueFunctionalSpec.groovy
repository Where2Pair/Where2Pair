package org.where2pair.venue.functional

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Specification

class VenueFunctionalSpec extends Specification {
    public static final String VENUE_NAME = "my test venue"

    def "store and retrieve venue though the REST api"() {
        given:
        Map venueJson = [name: VENUE_NAME,
                latitude: 1.0,
                longitude: 0.5,
                openHours: ["monday": [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 0]]]]

        when:
        def retrievedVenue = storeAndRetrieve(venueJson)

        then:
        retrievedVenue.name == VENUE_NAME
    }

    private storeAndRetrieve(venueJson) {
        def where2pair = new RESTClient("http://localhost:5050/")
        //where2pair.auth.basic("testUser", "password")

        def putResponse = where2pair.post(path: "venue", body: venueJson, requestContentType: ContentType.JSON)
        assert putResponse.status == 200
        long savedVenueId = putResponse.data.id

        def getResponse = where2pair.get(path: "venue/$savedVenueId", requestContentType: ContentType.URLENC)
        getResponse.data
    }
}
