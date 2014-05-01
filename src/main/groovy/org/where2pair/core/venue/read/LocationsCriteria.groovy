package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.Coordinates

import static org.where2pair.core.venue.read.DistanceUnit.MILES

@Immutable
@ToString
class LocationsCriteria {

    List<Coordinates> locations
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
