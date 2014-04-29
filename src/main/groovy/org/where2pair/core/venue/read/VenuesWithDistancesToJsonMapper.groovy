package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Coordinates

@Immutable
class VenuesWithDistancesToJsonMapper {
    final VenueToJsonMapper venueToJsonMapper

    List<Map<String, ?>> toJson(List<VenueWithDistances> venuesWithDistances) {
        venuesWithDistances.collect {
            [
                distances: coordinateDistancesToJson(it.distances),
                averageDistance: distanceToJson(it.averageDistance),
                venue: venueToJsonMapper.toJson(it.venue)
            ]
        }
    }

    private def coordinateDistancesToJson(Map<Coordinates, Distance> distances) {
        distances.collect {
            [location: it.key, distance: distanceToJson(it.value)]
        }
    }

    private Map<String, ?> distanceToJson(Distance distance) {
        [value: distance.value, unit: distance.unit.toString().toLowerCase()]
    }
}
