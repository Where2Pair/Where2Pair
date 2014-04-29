package org.where2pair.core.venue.common

import groovy.transform.Immutable

@Immutable
class VenueId {
    String venueName
    Coordinates location
    String addressLine1

//    @Override
//    String toString() {
//        String nameComponent = venueName.replaceAll(' ', '_')
//        String coordinatesComponent = "${location.lat}_${location.lng}"
//        String addressLine1Component = addressLine1
//        "${nameComponent}_${coordinatesComponent}_${addressLine1Component}"
//    }
}
