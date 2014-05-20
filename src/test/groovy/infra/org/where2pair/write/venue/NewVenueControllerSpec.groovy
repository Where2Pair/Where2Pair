package org.where2pair.write.venue

import org.where2pair.common.venue.StatusCode
import spock.lang.Specification
import spock.lang.Unroll

import static groovy.json.JsonOutput.toJson
import static org.where2pair.common.venue.NewVenueIdBuilder.aVenueId
import static org.where2pair.read.venue.VenueBuilder.aVenue

class NewVenueControllerSpec extends Specification {

    def newVenueService = Mock(NewVenueService)
    def controller = new NewVenueController(newVenueService: newVenueService)
    def venueJsonMap = aVenue().toJson()
    def venueJsonString = toJson(venueJsonMap)

    def 'saves new venues and returns venue id'() {
        given:
        def expectedVenueId = aVenueId().build()
        newVenueService.save(venueJsonMap) >> expectedVenueId
        def expectedJsonResponse = toJson([venueId: expectedVenueId.encode()])

        when:
        def response = controller.save(venueJsonString)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.OK
    }

    def 'returns an error message if there was a problem whilst saving'() {
        given:
        newVenueService.save(venueJsonMap) >> { throw new InvalidVenueJsonException('Could not parse Json') }
        def expectedJsonResponse = toJson([error: 'Could not parse Json'])

        when:
        def response = controller.save(venueJsonString)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.BAD_REQUEST
    }

    @Unroll
    def 'returns an error message if the json is not in the expected format'() {
        given:
        def invalidVenueJsonString = toJson(invalidVenueJson)

        when:
        def response = controller.save(invalidVenueJsonString)

        then:
        response.responseBody == expectedErrorMessage
        response.statusCode == StatusCode.BAD_REQUEST

        where:
        invalidVenueJson | expectedErrorMessage
        []               | toJson([error: 'Venue json not in the expected format'])
        ''               | toJson([error: 'Venue json not in the expected format'])
    }
}
