package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class NewVenue {
    private final Map<String, ?> venueJson

    NewVenue(Map<String, ?> venueJson) {
        this.venueJson = venueJson
    }

    static NewVenueSavedEvent publishNewVenue(Map<String, ?> venueJson) throws InvalidVenueJsonException {
        new VenueJsonValidator().validate(venueJson)
        new NewVenueSavedEvent(venueIdFrom(venueJson), new NewVenue(venueJson))
    }

    private static NewVenueId venueIdFrom(Map<String, ?> venueJson) {
        new NewVenueId(venueJson.name,
                venueJson.location.latitude,
                venueJson.location.longitude,
                venueJson.address.addressLine1)
    }
}
