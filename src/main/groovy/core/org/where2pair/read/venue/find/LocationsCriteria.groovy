package org.where2pair.read.venue.find

import static org.where2pair.read.venue.DistanceUnit.MILES

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.read.venue.Coordinates
import org.where2pair.read.venue.Distance
import org.where2pair.read.venue.DistanceUnit
import org.where2pair.read.venue.Venue

@Immutable
@ToString
class LocationsCriteria {

    Collection<Coordinates> locations
    DistanceUnit distanceUnit

    static LocationsCriteria distanceInMilesTo(Coordinates... coordinates) {
        new LocationsCriteria(locations: coordinates, distanceUnit: MILES)
    }

    Map<Coordinates, Distance> distancesTo(Venue venue) {
        locations.collectEntries { coordinates ->
            [(coordinates): venue.distanceTo(coordinates, distanceUnit)]
        }
    }

}

