package org.where2pair.write.venue

import org.where2pair.common.venue.VenueId


class NewVenueSavedEvent {
    final VenueId venueId
    final NewVenue newVenue

    NewVenueSavedEvent(VenueId venueId, NewVenue newVenue) {
        this.venueId = venueId
        this.newVenue = newVenue
    }
}
