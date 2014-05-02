package org.where2pair.infra.venue.persist

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.read.Venue
import org.where2pair.infra.venue.persistence.HashMapVenueCache
import spock.lang.Specification

import static org.where2pair.core.venue.VenueIdBuilder.aRandomVenueId
import static org.where2pair.core.venue.read.VenueBuilder.aVenue

class HashMapVenueCacheSpec extends Specification {

    HashMapVenueCache hashMapVenueCache = new HashMapVenueCache()

    def 'stores venues by id'() {
        given:
        VenueId venueId1 = aRandomVenueId()
        VenueId venueId2 = aRandomVenueId()
        Venue venue1 = aVenue().withId(venueId1).build()
        Venue venue2 = aVenue().withId(venueId2).build()
        hashMapVenueCache.put(venue1)
        hashMapVenueCache.put(venue2)

        when:
        Venue result = hashMapVenueCache.get(venueId2)

        then:
        result == venue2

        when:
        result = hashMapVenueCache.get(venueId1)

        then:
        result == venue1
    }

    def 'returns null when venues do not exist'() {
        when:
        Venue fetchedVenue = hashMapVenueCache.get(aRandomVenueId())

        then:
        fetchedVenue == null
    }

}
