package org.where2pair.main.venue

import org.where2pair.main.HashMapVenueCache
import org.where2pair.read.venue.Venue
import spock.lang.Specification

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.read.venue.VenueIdBuilder.aRandomVenueId

class HashMapVenueCacheSpec extends Specification {

    def hashMapVenueCache = new HashMapVenueCache()

    def 'stores venues by id'() {
        given:
        def venueId1 = aRandomVenueId()
        def venueId2 = aRandomVenueId()
        def venue1 = aVenue().withId(venueId1).build()
        def venue2 = aVenue().withId(venueId2).build()
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
