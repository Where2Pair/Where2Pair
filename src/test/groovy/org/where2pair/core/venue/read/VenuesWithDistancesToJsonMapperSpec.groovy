package org.where2pair.core.venue.read

import spock.lang.Specification

import static org.where2pair.core.venue.read.DistanceBuilder.fromCoordinates
import static org.where2pair.core.venue.read.VenueWithDistancesBuilder.aVenue

class VenuesWithDistancesToJsonMapperSpec extends Specification {

    OpenHoursToJsonMapper openHoursToJsonMapper = new OpenHoursToJsonMapper()
    VenueToJsonMapper venueToJsonMapper = new VenueToJsonMapper(openHoursToJsonMapper)
    VenuesWithDistancesToJsonMapper venueWithDistancesToJsonMapper = new VenuesWithDistancesToJsonMapper(venueToJsonMapper)

    def 'converts VenueWithDistances to json'() {
        given:
        def venue1 = aVenue()
                .withDistance(fromCoordinates(1.0, 0.1).miles(1))
                .withDistance(fromCoordinates(2.0, 0.2).miles(2))
        def venue2 = aVenue()
                .withDistance(fromCoordinates(3.0, 0.3).miles(3))
                .withDistance(fromCoordinates(4.0, 0.4).miles(4))

        and:
        def venuesWithDistances = [venue1.build(), venue2.build()]
        def expectedJson = [venue1.toJson(), venue2.toJson()]

        when:
        def venueWithDistancesJson = venueWithDistancesToJsonMapper.toJson(venuesWithDistances)

        then:
        venueWithDistancesJson == expectedJson
    }
}
