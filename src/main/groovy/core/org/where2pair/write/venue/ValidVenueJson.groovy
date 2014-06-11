package org.where2pair.write.venue

import static org.where2pair.write.venue.VenueJsonValidator.validate

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class ValidVenueJson {
    final VenueJson venueJson

    ValidVenueJson(VenueJson venueJson) {
        validate(venueJson)
        this.venueJson = venueJson
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
