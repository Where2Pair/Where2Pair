package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode
@TupleConstructor
class NewVenue {
    final VenueJson venueJson

    static NewVenueSavedEvent publishNewVenue(VenueJson venueJson) throws InvalidVenueJsonException {
        new VenueJsonValidator().validate(venueJson)
        new NewVenueSavedEvent(venueIdFrom(venueJson), new NewVenue(venueJson))
    }

    private static NewVenueId venueIdFrom(VenueJson venueJson) {
        Map<String, ?> venueJsonMap = venueJson.jsonMap
        new NewVenueId(venueJsonMap.name,
                venueJsonMap.location.latitude,
                venueJsonMap.location.longitude,
                venueJsonMap.address.addressLine1)
    }

    Map<String, ?> getJsonMap() {
        venueJson.jsonMap
    }
}
