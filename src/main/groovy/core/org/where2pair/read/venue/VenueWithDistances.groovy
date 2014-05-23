package org.where2pair.read.venue

import groovy.transform.ToString
import groovy.transform.Immutable
import org.where2pair.common.venue.Coordinates

@Immutable
@ToString
class VenueWithDistances {
    Venue venue
    Map<Coordinates, Distance> distances

    Distance getAverageDistance() {
        double averageValue = distances.values().value.sum() / distances.size()
        DistanceUnit unit = distances.values()[0].unit
        new Distance(averageValue, unit)
    }
}

