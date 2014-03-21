package org.where2pair.infra.venue.web

import org.where2pair.core.venue.Venue
import org.where2pair.core.venue.VenueSaveOrUpdater

public class SaveVenueController {

    VenueSaveOrUpdater venueSaveOrUpdater
    VenueJsonMarshaller venueJsonMarshaller

    def save(Map json) {
        Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueSaveOrUpdater.save(venue)
        json
    }

}