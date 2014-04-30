package org.where2pair.core.venue.common

import spock.lang.Specification

import static org.where2pair.core.venue.VenueIdBuilder.aVenueId

class VenueIdSpec extends Specification {

    def 'provides string representation'() {
        given:
        def venueId = aVenueId().withName('venue name')
                .withLatitude(1.0)
                .withLongitude(0.1)
                .withAddressLine1('address line 1').build()

        when:
        String venueIdAsString = venueId.toString()

        then:
        venueIdAsString == 'venue_name|1.0|0.1|address_line_1'
    }
}
