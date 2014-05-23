package org.where2pair.read.venue

import org.where2pair.common.venue.Coordinates

class VenueWithDistancesBuilder {

    private List<DistanceBuilder.DistanceToCoordinates> distances = []
    @Delegate
    VenueBuilder venueBuilder = new VenueBuilder()

    static VenueWithDistancesBuilder aVenue() {
        new VenueWithDistancesBuilder()
    }

    private VenueWithDistancesBuilder() {
    }

    VenueWithDistancesBuilder withDistance(DistanceBuilder.DistanceToCoordinates distanceToCoordinates) {
        distances << distanceToCoordinates
        this
    }

    VenueWithDistances build() {
        Venue venue = venueBuilder.build()
        Map<Coordinates, Distance> distancesMap = distances.collectEntries { [it.coordinates, it.distance] }
        new VenueWithDistances(venue: venue, distances: distancesMap)
    }

    Map<String, ?> toJson() {
        VenueWithDistances venueWithDistances = build()
        [
                distances: venueWithDistances.distances.collect {
                    [location: it.key, distance: distanceAsJson(it.value)]
                },
                averageDistance: distanceAsJson(venueWithDistances.averageDistance),
                venue: venueBuilder.toJson()
        ]

    }

    def distanceAsJson(Distance distance) {
        [value: distance.value, unit: distance.unit.toString().toLowerCase()]
    }
}

