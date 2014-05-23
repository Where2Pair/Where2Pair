package org.where2pair.read.venue.mappingfromjson

import static org.where2pair.read.venue.VenueDetailsBuilder.venueDetails

import spock.lang.Specification

class JsonToVenueDetailsMapperSpec extends Specification {

    def jsonToVenueDetailsMapper = new JsonToVenueDetailsMapper()

    def 'maps json to venue details'() {
        given:
        def expectedVenueDetails = venueDetails().build()
        def venueDetailsJson = venueDetails().toJson()

        when:
        def venueDetails = jsonToVenueDetailsMapper.toVenueDetails(venueDetailsJson)

        then:
        venueDetails == expectedVenueDetails
    }
}

