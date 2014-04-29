package org.where2pair.infra.venue.web

import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository

class ShowVenueController {
    VenueRepository venueRepository
    VenueJsonMarshaller venueJsonMarshaller

    def show(String id) {
        Venue venue = venueRepository.get(id)

        if (venue) {
            Map venueJson = venueJsonMarshaller.asVenueJson(venue)
            return venueJson
        } else {
            return new ErrorResponse(message: "Venue with id $id could not be found", status: 404)
        }
    }

    def showAll() {
        List venues = venueRepository.getAll()
        List venuesJson = venueJsonMarshaller.asVenuesJson(venues)
        venuesJson
    }

}
