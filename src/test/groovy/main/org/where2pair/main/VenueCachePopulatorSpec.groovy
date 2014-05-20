package org.where2pair.main

import org.where2pair.write.venue.NewVenue
import org.where2pair.write.venue.NewVenueSavedEvent
import spock.lang.Specification

import static org.where2pair.common.venue.NewVenueIdBuilder.aVenueId
import static org.where2pair.read.venue.VenueBuilder.aVenue


class VenueCachePopulatorSpec extends Specification {

    def venueCache = Mock(HashMapVenueCache)
    def venueCachePopulator = new VenueCachePopulator(venueCache)

    def 'populates venue cache with deserialized venues'() {
        given:
        def venueBuilder = aVenue()
        def newVenue = new NewVenue(venueBuilder.toJson())
        def newVenueSavedEvent = new NewVenueSavedEvent(aVenueId().build(), newVenue)

        when:
        venueCachePopulator.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        venueCache.put(venueBuilder.build())
    }

}
