package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Coordinates

@Immutable
class VenueDetails {
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    LinkedHashSet<String> facilities
}
