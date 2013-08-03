package org.where2pair.grails

import grails.test.mixin.TestMixin
import grails.test.mixin.web.ControllerUnitTestMixin
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient
import spock.lang.Specification

class VenueIntegrationSpec extends Specification {
    public static final String VENUE_NAME = "my test venue"

    def "store and retrieve venue though the REST api"() {
        given:
        VenueDTO venueDTO = new VenueDTO(name: VENUE_NAME, latitude: 1.0, longitude: 0.5,
                openHours: ["monday": [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 0]]])

        when:
        def retrievedJson = storeAndRetrieve(venueDTO)

        then:
        retrievedJson.name == VENUE_NAME
    }

    private net.sf.json.JSON storeAndRetrieve(venueDTO) {
        def venueJson = new grails.converters.JSON(venueDTO)
        def where2pair = new RESTClient("http://localhost:8080/Where2Pair")

        def venueJsonString = venueJson.toString(true)
        def savedVenue = where2pair.post(path: "venue", body: venueDTO, requestContentType: ContentType.JSON)

        def id = savedVenue.id;

        def resp = where2pair.get(path: "venue/$id", requestContentType: ContentType.URLENC)

        resp.data
    }
}
