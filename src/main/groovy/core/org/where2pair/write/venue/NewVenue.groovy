package org.where2pair.write.venue

import static org.where2pair.write.venue.VenueJsonValidator.validate

import groovy.transform.Immutable

@Immutable
class NewVenue {
    VenueJson venueJson

    static NewVenueSavedEvent publishNewVenue(VenueJson venueJson) throws InvalidVenueJsonException {
        validate(venueJson)
        new NewVenueSavedEvent(new NewVenue(venueJson))
    }

    NewVenueId getVenueId() {
        new NewVenueId(venueJson.name,
                venueJson.location.latitude,
                venueJson.location.longitude,
                venueJson.address.addressLine1)
    }

    String getVenueJsonPayload() {
        venueJson.payload
    }
}
