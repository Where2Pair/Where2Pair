package org.where2pair.main

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.write.venue.VenueJsonBuilder.venueJson

import org.where2pair.write.venue.NewVenue
import org.where2pair.write.venue.NewVenueSavedEvent
import spock.lang.Specification

class VenueCachePopulatorSpec extends Specification {

    def venueCache = Mock(HashMapVenueCache)
    def venueCachePopulator = new VenueCachePopulator(venueCache)

    def 'populates venue cache with deserialized venues'() {
        given:
        def venueBuilder = aVenue()
        def newVenue = new NewVenue(venueJson().build())
        def newVenueSavedEvent = new NewVenueSavedEvent(newVenue)
        def expectedVenue = venueBuilder.build()

        when:
        venueCachePopulator.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        venueCache.put(expectedVenue)
    }

}

