package org.where2pair.write.venue

import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode
@TupleConstructor
class NewVenueSavedEvent {
    @Delegate final ValidVenueJson newVenue

    static NewVenueSavedEvent create(RawVenueJson rawVenueJson) {
        def venueJson = new VenueJson(rawVenueJson)
        def validVenueJson = new ValidVenueJson(venueJson)
        new NewVenueSavedEvent(validVenueJson)
    }
}

