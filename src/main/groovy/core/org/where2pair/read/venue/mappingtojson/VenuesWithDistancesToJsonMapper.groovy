package org.where2pair.read.venue.mappingtojson

import groovy.transform.Immutable
import org.where2pair.common.venue.Coordinates
import org.where2pair.read.venue.Distance
import org.where2pair.read.venue.VenueWithDistances

@Immutable
class VenuesWithDistancesToJsonMapper {

    private VenueToJsonMapper venueToJsonMapper = new VenueToJsonMapper()

    List<Map<String, ?>> toJsonStructure(List<VenueWithDistances> venuesWithDistances) {
        venuesWithDistances.collect {
            [
                distances: coordinateDistancesToJson(it.distances),
                averageDistance: distanceToJson(it.averageDistance),
                venue: venueToJsonMapper.toJsonStructure(it.venue)
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
