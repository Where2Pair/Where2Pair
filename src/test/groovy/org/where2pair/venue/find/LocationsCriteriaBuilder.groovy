package org.where2pair.venue.find

import org.where2pair.venue.Coordinates

class LocationsCriteriaBuilder {

    Map locations = [:]

    static LocationsCriteriaBuilder locationsCriteria() {
        new LocationsCriteriaBuilder()
    }

    LocationsCriteriaBuilder withLocation(lat, lng) {
        locations["location${locations.size()}"] = new Coordinates(lat, lng)
        this
    }

    LocationsCriteriaBuilder withLocation(coords) {
        locations["location${locations.size()}"] = coords
        this
    }

    LocationsCriteriaBuilder with(locations) {
        this.locations = locations.collectEntries {
            def value = it.value

            if (value instanceof Coordinates) return [(it.key): value]
            else return [(it.key): new Coordinates(value[0], value[1])]
        }
        this
    }

    LocationsCriteria withDistanceUnit(distanceUnit) {
        new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
    }
}
