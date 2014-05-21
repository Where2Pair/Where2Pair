package org.where2pair.read.venue.mappingToJson

import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper
import spock.lang.Specification

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.read.venue.VenueDetailsBuilder.venueDetails

class VenueToJsonMapperSpec extends Specification {

    def venueBuilder = aVenue()
    def venue = venueBuilder.build()
    def venueJson = venueBuilder.toJson()
    def venueToJsonMapper = new VenueToJsonMapper()

    def 'converts Venue to json'() {
        when:
        Map result = venueToJsonMapper.toJson(venue)

        then:
        result == venueJson
    }

    def 'converts Venues to json'() {
        given:
        def venueBuilder = aVenue().with(venueDetails().withName('another venue name'))
        def anotherVenue = venueBuilder.build()
        def anotherVenueJson = venueBuilder.toJson()

        when:
        List result = venueToJsonMapper.toJson([venue, anotherVenue])

        then:
        result == [venueJson, anotherVenueJson]
    }
}
