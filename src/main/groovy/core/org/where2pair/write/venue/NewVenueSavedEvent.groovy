package org.where2pair.write.venue

import groovy.transform.TupleConstructor

@TupleConstructor
class NewVenueSavedEvent {
    final NewVenueId venueId
    final NewVenue newVenue
}
