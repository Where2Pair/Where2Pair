package org.where2pair.infra.venue.persistence

import org.where2pair.core.venue.read.JsonToVenueDetailsMapper
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueDetails
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber

class AsyncVenueCachePopulator implements NewVenueSavedEventSubscriber {

    final HashMapVenueCache venueCache
    final JsonToVenueDetailsMapper jsonToVenueDetailsMapper

    public AsyncVenueCachePopulator(HashMapVenueCache venueCache, JsonToVenueDetailsMapper jsonToVenueDetailsMapper) {
        this.venueCache = venueCache
        this.jsonToVenueDetailsMapper = jsonToVenueDetailsMapper
    }

    @Override
    public void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        //TODO
        //Implement some producer/consumer pattern
        //Does GPars offer some nice support here?

        VenueDetails venueDetails = jsonToVenueDetailsMapper.toVenueDetails(newVenueSavedEvent.newVenue.venueJson)
        Venue venue = new Venue(newVenueSavedEvent.venueId, venueDetails)
        venueCache.put(venue)
    }
}
