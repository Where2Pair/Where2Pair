package org.where2pair.infra.venue.write

import org.where2pair.core.venue.read.JsonToVenueDetailsMapper
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueDetails
import org.where2pair.core.venue.write.NewVenue
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber
import org.where2pair.infra.venue.persistence.HashMapVenueCache

class VenueCachePopulator {

    final VenueCache venueCache
    final JsonToVenueDetailsMapper jsonToVenueDetailsMapper

    public VenueCachePopulator(HashMapVenueCache venueCache, JsonToVenueDetailsMapper jsonToVenueDetailsMapper) {
        this.venueCache = venueCache
        this.jsonToVenueDetailsMapper = jsonToVenueDetailsMapper
    }

    @Override
    public void put(NewVenue newVenue) {
        VenueDetails venueDetails = jsonToVenueDetailsMapper.toVenueDetails(newVenue.venueJson)
        Venue venue = new Venue(newVenueSavedEvent.venueId, venueDetails)
        venueCache.put(venue)
    }
}
