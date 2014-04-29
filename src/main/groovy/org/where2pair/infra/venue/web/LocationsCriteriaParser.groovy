package org.where2pair.infra.venue.web

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.LocationsCriteria

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
