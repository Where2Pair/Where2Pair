package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.VenueId

class NewVenueService {

    NewVenueSavedEventPublisher newVenueSavedEventPublisher

    VenueId save(Map<String, ?> venueJson) {
        NewVenueSavedEvent newVenueSavedEvent = NewVenue.publishNewVenue(venueJson)
        newVenueSavedEventPublisher.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}
