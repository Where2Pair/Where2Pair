package org.where2pair.read.venue

import groovy.transform.Immutable
import org.where2pair.common.venue.Coordinates

@Immutable
class VenueDetails {
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    FacilityStatuses facilities
}

