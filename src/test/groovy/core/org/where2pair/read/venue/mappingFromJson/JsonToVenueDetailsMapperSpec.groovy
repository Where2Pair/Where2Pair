package org.where2pair.read.venue.mappingFromJson

import org.where2pair.read.venue.mappingfromjson.JsonToVenueDetailsMapper
import spock.lang.Specification

import static org.where2pair.read.venue.VenueDetailsBuilder.venueDetails

class JsonToVenueDetailsMapperSpec extends Specification {

    JsonToVenueDetailsMapper jsonToVenueDetailsMapper = new JsonToVenueDetailsMapper()

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
