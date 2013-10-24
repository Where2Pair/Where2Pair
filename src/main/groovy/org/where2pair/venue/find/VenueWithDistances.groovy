package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

import org.where2pair.venue.Distance
import org.where2pair.venue.DistanceUnit
import org.where2pair.venue.Venue

@EqualsAndHashCode
@ToString
class VenueWithDistances {
    Venue venue
    Map distances

    Distance getAverageDistance() {
		double averageValue = distances.values().value.sum() / distances.size()
		DistanceUnit unit = distances.values()[0].unit
        new Distance(value: averageValue, unit: unit)
    }
}
