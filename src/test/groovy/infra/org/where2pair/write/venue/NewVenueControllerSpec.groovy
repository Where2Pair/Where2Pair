package org.where2pair.write.venue

import static NewVenueIdBuilder.aVenueId
import static groovy.json.JsonOutput.toJson
import static org.where2pair.write.venue.VenueJsonBuilder.venueJson

import org.where2pair.common.venue.StatusCode
import spock.lang.Specification

class NewVenueControllerSpec extends Specification {

    def newVenueService = Mock(NewVenueService)
    def controller = new NewVenueController(newVenueService: newVenueService)
    def venueJson = venueJson().build()
    def venueJsonString = venueJson.rawVenueJson

    def 'saves new venues and returns venue id'() {
        given:
        def expectedVenueId = aVenueId().build()
        newVenueService.save(venueJson) >> expectedVenueId
        def expectedJsonResponse = toJson([venueId: expectedVenueId.toString()])

        when:
        def response = controller.save(venueJsonString)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.OK
    }

    def 'returns an error message if there was a problem whilst saving'() {
        given:
        newVenueService.save(venueJson) >> { throw new InvalidVenueJsonException('Could not parse Json') }
        def expectedJsonResponse = toJson([error: 'Could not parse Json'])

        when:
        def response = controller.save(venueJsonString)

        then:
        response.responseBody == expectedJsonResponse
        response.statusCode == StatusCode.BAD_REQUEST
    }

    def 'returns an error message if the json is not in the expected format'() {
        given:
        newVenueService.save(_) >> { throw new InvalidVenueJsonException('error message') }

        when:
        def response = controller.save('invalid json')

        then:
        response.responseBody == toJson([error: 'error message'])
        response.statusCode == StatusCode.BAD_REQUEST
    }

}

