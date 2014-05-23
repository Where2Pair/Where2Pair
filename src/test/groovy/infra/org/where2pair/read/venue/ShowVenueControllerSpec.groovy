package org.where2pair.read.venue

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.read.venue.VenueIdBuilder.aRandomVenueId

import groovy.json.JsonOutput
import org.where2pair.common.venue.StatusCode
import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper
import spock.lang.Specification

class ShowVenueControllerSpec extends Specification {

    def venueRepository = Mock(VenueRepository)
    def venueToJsonMapper = new VenueToJsonMapper()
    def controller = new ShowVenueController(venueRepository, venueToJsonMapper)

    def 'looks up and renders venues as json'() {
        given:
        def venueId = aRandomVenueId()
        def venue = aVenue().build()
        venueRepository.get(venueId) >> venue
        def expectedVenueJson = venueToJsonMapper.toJsonStructure(venue)

        when:
        def response = controller.show(venueId.toString())

        then:
        response.responseBody == JsonOutput.toJson(expectedVenueJson)
        response.statusCode == StatusCode.OK
    }

    def 'shows 404 error response if venue was not found'() {
        def venueId = aRandomVenueId()
        venueRepository.get(venueId) >> null

        when:
        def response = controller.show(venueId.toString())

        then:
        response.responseBody == JsonOutput.toJson([error: "Venue with id ${venueId.toString()} could not be found"])
        response.statusCode == StatusCode.NOT_FOUND
    }

    def 'shows 404 error response even if venue id is malformed'() {
        def venueId = 'invalid-venue-id'

        when:
        def response = controller.show(venueId)

        then:
        response.responseBody == JsonOutput.toJson([error: "Venue with id ${venueId.toString()} could not be found"])
        response.statusCode == StatusCode.NOT_FOUND
    }
}
