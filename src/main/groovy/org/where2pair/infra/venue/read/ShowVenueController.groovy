package org.where2pair.infra.venue.read

import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository

class ShowVenueController {
    VenueRepository venueRepository

    def show(String id) {
        Venue venue = venueRepository.get(id)

        if (venue) {
            Map venueJson = venueJsonMarshaller.asVenueJson(venue)
            return venueJson
        } else {
            return null
        }
    }

    def showAll() {
        List venues = venueRepository.getAll()
        List venuesJson = venueJsonMarshaller.asVenuesJson(venues)
        venuesJson
    }

}
