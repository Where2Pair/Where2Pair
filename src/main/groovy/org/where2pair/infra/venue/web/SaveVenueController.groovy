package org.where2pair.infra.venue.web

import org.where2pair.core.venue.Venue
import org.where2pair.core.venue.VenueService

public class SaveVenueController {

    VenueService venueService
    VenueJsonMarshaller venueJsonMarshaller

    def save(Map json) {
        Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueService.save(venue)
        json
    }

}