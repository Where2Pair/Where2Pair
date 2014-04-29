package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.VenueId

class NewVenueService {

    NewVenueSavedEventSubscribers newVenueSavedEventSubscribers

    VenueId save(Map<String, ?> venueJson) {
        NewVenueSavedEvent newVenueSavedEvent = NewVenue.publishNewVenue(venueJson)
        newVenueSavedEventSubscribers.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}
