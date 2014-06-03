package org.where2pair.write.venue

import groovy.transform.Immutable

@Immutable
class NewVenueSavedEvent {
    @Delegate NewVenue newVenue
}

