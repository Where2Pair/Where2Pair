package org.where2pair.write.venue

import org.where2pair.common.venue.StatusCode
import org.where2pair.common.venue.VenueIdBuilder
import spock.lang.Specification

import static groovy.json.JsonOutput.toJson
import static org.where2pair.common.venue.VenueIdBuilder.aVenueId
import static org.where2pair.read.venue.VenueBuilder.aVenue

class NewVenueControllerSpec extends Specification {

    def newVenueService = Mock(NewVenueService)
    def controller = new NewVenueController(newVenueService: newVenueService)
    def venueJson = aVenue().toJson()

    def 'saves new venues and returns venue id'() {
        given:
        def expectedVenueId = aVenueId().build()
        newVenueService.save(venueJson) >> expectedVenueId
        def expectedJsonResponse = toJson([venueId: expectedVenueId.toString()])

        when:
        def response = controller.save(venueJson)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.OK
    }

    def 'returns the error message if there was a problem whilst saving'() {
        given:
        newVenueService.save(venueJson) >> { throw new InvalidVenueJsonException('Could not parse Json') }
        def expectedJsonResponse = toJson([error: 'Could not parse Json'])

        when:
        def response = controller.save(venueJson)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.BAD_REQUEST
    }
}
