package org.where2pair.main

import groovy.transform.TupleConstructor
import org.where2pair.read.venue.Venue
import org.where2pair.read.venue.VenueDetails
import org.where2pair.read.venue.mappingfromjson.JsonToVenueDetailsMapper
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueSavedEventSubscriber

@TupleConstructor
class VenueCachePopulator implements NewVenueSavedEventSubscriber {

    final HashMapVenueCache venueCache
    final JsonToVenueDetailsMapper jsonToVenueDetailsMapper = new JsonToVenueDetailsMapper()

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        VenueDetails venueDetails = jsonToVenueDetailsMapper.toVenueDetails(newVenueSavedEvent.newVenue.venueJson)
        Venue venue = Venue.newInstance(newVenueSavedEvent.venueId, venueDetails)
        venueCache.put(venue)
    }
}
