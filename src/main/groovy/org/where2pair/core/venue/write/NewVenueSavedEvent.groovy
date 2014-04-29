package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.VenueId


class NewVenueSavedEvent {
    final VenueId venueId
    final NewVenue newVenue

    NewVenueSavedEvent(VenueId venueId, NewVenue newVenue) {
        this.venueId = venueId
        this.newVenue = newVenue
    }
}
