package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode
import org.where2pair.common.venue.Coordinates

@EqualsAndHashCode
class NewVenue {
    final Map<String, ?> venueJson

    NewVenue(Map<String, ?> venueJson) {
        this.venueJson = venueJson
    }

    static NewVenueSavedEvent publishNewVenue(Map<String, ?> venueJson) throws InvalidVenueJsonException {
        new VenueJsonValidator().validate(venueJson)
        new NewVenueSavedEvent(venueIdFrom(venueJson), newVenue(venueJson))
    }

    private static NewVenueId venueIdFrom(Map<String, ?> venueJson) {
        new NewVenueId(venueJson.name,
                new Coordinates(venueJson.location.latitude, venueJson.location.longitude),
                venueJson.address.addressLine1)
    }

    private static NewVenue newVenue(Map<String, ?> venueJson) {
        new NewVenue(venueJson)
    }
}