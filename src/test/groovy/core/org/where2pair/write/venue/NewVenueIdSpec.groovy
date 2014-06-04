package org.where2pair.write.venue

import static NewVenueIdBuilder.aRandomVenueId
import static NewVenueIdBuilder.aVenueId

import spock.lang.Specification

class NewVenueIdSpec extends Specification {

    def 'provides unique string representation'() {
        given:
        List<NewVenueId> venueIds = []
        1000.times { venueIds << aRandomVenueId() }

        when:
        List<String> encodedVenueIds = venueIds*.toString()

        then:
        encodedVenueIds.unique().size() == 1000
    }

    def 'equivalent venue ids should encode the same'() {
        given:
        def venueIdBuilder = aVenueId()
        def venueId1 = venueIdBuilder.build()
        def venueId2 = venueIdBuilder.build()

        when:
        def encodedVenueId1 = venueId1.toString()
        def encodedVenueId2 = venueId2.toString()

        then:
        encodedVenueId1 == encodedVenueId2
    }
}

