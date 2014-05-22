package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.common.venue.Coordinates

import static org.where2pair.read.venue.DistanceUnit.MILES

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
