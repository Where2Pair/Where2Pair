package org.where2pair.write.venue

import org.where2pair.common.venue.VenueId

class NewVenueService {

    NewVenueSavedEventSubscribers newVenueSavedEventSubscribers

    VenueId save(Map<String, ?> venueJson) {
        NewVenueSavedEvent newVenueSavedEvent = NewVenue.publishNewVenue(venueJson)
        newVenueSavedEventSubscribers.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}
