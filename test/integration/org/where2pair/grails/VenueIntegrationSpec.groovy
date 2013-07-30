package org.where2pair.grails

import grails.plugin.spock.IntegrationSpec
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

class VenueIntegrationSpec extends IntegrationSpec {
    public static final String VENUE_NAME = "my test venue"

    def "store and retrieve venue though the REST api"() {
        given:
        VenueDTO venueDTO = new VenueDTO(name: VENUE_NAME, latitude: 1.0, longitude: 0.5,
                openHours: ["monday": [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 0]]])


        when:
        def retrievedJson = storeAndRetrieve(venueDTO)

        then:
        retrievedJson.latitude == venueDTO.latitude
        retrievedJson.longitude == venueDTO.longitude
    }

    private net.sf.json.JSON storeAndRetrieve(venueDTO) {
        def venueJson = new grails.converters.JSON(venueDTO)
        def where2pair = new RESTClient("http://localhost:8080")

        where2pair.post(path: "Where2Pair/venue", body: venueJson.toString(), requestContentType: ContentType.JSON)

        def resp = where2pair.get(path: "Where2Pair/venue/findNearest", query: "location1=" + venueDTO.latitude + ","
                + venueDTO.longitude, requestContentType: ContentType.URLENCJSON)
        resp.data
    }
}
