package org.where2pair.venue.save

import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller

public class SaveVenueController {

    VenueSaveOrUpdater venueSaveOrUpdater
    VenueJsonMarshaller venueJsonMarshaller

    def save(Map json) {
        Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueSaveOrUpdater.save(venue)
        json
    }

}