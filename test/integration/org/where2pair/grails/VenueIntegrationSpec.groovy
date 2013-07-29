package org.where2pair.grails

import grails.converters.JSON
import grails.plugin.spock.IntegrationSpec
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

class VenueIntegrationSpec extends IntegrationSpec {
    def "store and retrieve venue though the REST api"() {
        given:
        VenueDTO venueDTO = new VenueDTO(latitude: 1.0, longitude: 0.5,
                openHours: ["monday": [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 0]]])
        def venueJson = new JSON(venueDTO)

        def where2pair = new RESTClient("http://localhost:8080")

        when:
        def resp = where2pair.post(path: "Where2Pair/venue", body: venueJson.toString(), requestContentType: ContentType.JSON)
        def retrievedJson = resp.data

        then:
        retrievedJson.latitude == venueDTO.latitude
        retrievedJson.longitude == venueDTO.longitude
    }
}
