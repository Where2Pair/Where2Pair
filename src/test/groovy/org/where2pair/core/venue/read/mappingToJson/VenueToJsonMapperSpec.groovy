package org.where2pair.core.venue.read.mappingToJson

import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.mappingtojson.VenueToJsonMapper
import spock.lang.Specification

import static org.where2pair.core.venue.read.VenueBuilder.aVenue
import static org.where2pair.core.venue.read.VenueDetailsBuilder.venueDetails

class VenueToJsonMapperSpec extends Specification {

    Venue venue = aVenue().build()
    Map<String, ?> venueJson = aVenue().toJson()
    VenueToJsonMapper venueToJsonMapper = new VenueToJsonMapper()

    def 'converts Venue to json'() {
        when:
        Map result = venueToJsonMapper.toJson(venue)

        then:
        result == venueJson
    }

    def 'converts Venues to json'() {
        given:
        def venueBuilder = aVenue().with(venueDetails().withName('another venue name'))
        Venue anotherVenue = venueBuilder.build()
        Map<String, ?> anotherVenueJson = venueBuilder.toJson()

        when:
        List result = venueToJsonMapper.toJson([venue, anotherVenue])

        then:
        result == [venueJson, anotherVenueJson]
    }
}
