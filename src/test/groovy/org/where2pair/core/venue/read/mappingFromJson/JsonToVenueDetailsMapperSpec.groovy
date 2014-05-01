package org.where2pair.core.venue.read.mappingFromJson

import org.where2pair.core.venue.read.VenueDetails
import org.where2pair.core.venue.read.mappingfromjson.JsonToVenueDetailsMapper
import spock.lang.Specification

import static org.where2pair.core.venue.read.VenueDetailsBuilder.venueDetails


class JsonToVenueDetailsMapperSpec extends Specification {

    JsonToVenueDetailsMapper jsonToVenueDetailsMapper = new JsonToVenueDetailsMapper()

    def 'maps json to venue details'() {
        given:
        VenueDetails expectedVenueDetails = venueDetails().build()
        Map<String, ?> venueDetailsJson = venueDetails().toJson()

        when:
        VenueDetails venueDetails = jsonToVenueDetailsMapper.toVenueDetails(venueDetailsJson)

        then:
        venueDetails == expectedVenueDetails
    }
}
