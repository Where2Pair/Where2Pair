package org.where2pair.core.venue

import org.where2pair.core.venue.Venue
import org.where2pair.core.venue.VenueRepository

class VenueSaveOrUpdater {

    VenueRepository venueRepository

    long save(Venue venue) {
        Venue existingVenue = venueRepository.findByNameAndCoordinates(venue.name, venue.location)

        if (existingVenue) {
            venue.id = existingVenue.id
            venueRepository.update(venue)
            return venue.id
        } else {
            return venueRepository.save(venue)
        }
    }

}
