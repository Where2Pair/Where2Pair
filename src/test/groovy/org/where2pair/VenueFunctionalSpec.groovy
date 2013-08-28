package org.where2pair

//import groovyx.net.http.ContentType
//import groovyx.net.http.RESTClient
import spock.lang.Ignore
import spock.lang.Specification

class VenueFunctionalSpec extends Specification {
    public static final String VENUE_NAME = "my test venue"

	@Ignore
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

//    private JSON storeAndRetrieve(venueJson) {
//        def where2pair = new RESTClient("http://localhost:8080")
//        where2pair.auth.basic("testUser", "password")
//
//        def response = where2pair.post(path: "Where2Pair/venue", body: venueJson, requestContentType: ContentType.JSON)
//        assert response.status == 200
//
//        JSdefON savedVenue = response.data
//        where2pair.get(path: "Where2Pair/venue/${savedVenue.id}", requestContentType: ContentType.URLENC).data
//    }
}
