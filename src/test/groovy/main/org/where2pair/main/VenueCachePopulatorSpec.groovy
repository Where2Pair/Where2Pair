package org.where2pair.main

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.write.venue.RawVenueJsonBuilder.rawVenueJson

import org.where2pair.write.venue.NewVenueSavedEvent
import spock.lang.Specification

class VenueCachePopulatorSpec extends Specification {
    def venueCache = Mock(HashMapVenueCache)
    def venueCachePopulator = new VenueCachePopulator(venueCache)

    def 'populates venue cache with deserialized venues'() {
        given:
        def expectedVenue = aVenue().build()
        def newVenueSavedEvent = NewVenueSavedEvent.create(rawVenueJson().build())

        when:
        venueCachePopulator.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        venueCache.put(expectedVenue)
    }

}

