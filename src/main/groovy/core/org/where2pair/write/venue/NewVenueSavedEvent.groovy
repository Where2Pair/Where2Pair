package org.where2pair.write.venue

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.VenueId

@TupleConstructor
class NewVenueSavedEvent {
    final VenueId venueId
    final NewVenue newVenue
}
