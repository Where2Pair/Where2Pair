package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode
import groovy.transform.Immutable
import org.where2pair.common.venue.Coordinates
import org.where2pair.common.venue.VenueId


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

    private static VenueId venueIdFrom(Map<String, ?> venueJson) {
        new VenueId(venueJson.name,
                new Coordinates(venueJson.location.latitude, venueJson.location.longitude),
                venueJson.address.addressLine1)
    }

    private static NewVenue newVenue(Map<String, ?> venueJson) {
        new NewVenue(venueJson)
    }
}