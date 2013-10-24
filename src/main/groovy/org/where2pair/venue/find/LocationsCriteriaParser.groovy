package org.where2pair.venue.find

import org.where2pair.venue.Coordinates

import static java.lang.Double.parseDouble

class LocationsCriteriaParser {

    LocationsCriteria parse(Map params) {
        List locations = params.find { it.key == 'location' }?.value.collect {
            def (lat, lng) = it.split(',').collect { parseDouble(it) }
            new Coordinates(lat, lng)
        }

        def distanceUnit = params.distanceUnit ?: 'km'

        new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
    }

}
