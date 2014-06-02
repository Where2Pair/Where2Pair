package org.where2pair.read.venue

import groovy.transform.Immutable

@Immutable
class VenueDetails {
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    FacilityStatuses facilities
}

