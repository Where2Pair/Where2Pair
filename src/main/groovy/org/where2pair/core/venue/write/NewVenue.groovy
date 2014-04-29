package org.where2pair.core.venue.write

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.VenueId

@Immutable
class NewVenue {
    final Map<String, ?> venueJson

    static NewVenueSavedEvent publishNewVenue(Map<String, ?> venueJson) {
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