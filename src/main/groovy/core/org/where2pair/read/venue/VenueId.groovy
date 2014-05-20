package org.where2pair.read.venue

import groovy.transform.Immutable

@Immutable
class VenueId {
    String id

    @Override
    String toString() {
        id
    }
}
