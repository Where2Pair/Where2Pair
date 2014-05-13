package org.where2pair.infra.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.DistanceUnit
import org.where2pair.core.venue.read.LocationsCriteria

import static java.lang.Double.parseDouble

class LocationsCriteriaParser {

    LocationsCriteria parse(Map params) {
        def locationParams = params.findAll { it.key == 'location' }
        List locations = locationParams?.collect {
            def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
            new Coordinates(lat, lng)
        }

        if (locations.size() == 0) {
            throw new QueryParseException('Missing locations from request parameters. Query expected to be in the form: nearest?location1=x1,y1&location2=x2,y2...')
        }

        def distanceUnit = parseDistanceUnit(params)

        if (!distanceUnitValid(distanceUnit)) {
            throw new QueryParseException("Distance unit '$distanceUnit' is invalid. Use either 'KM' or 'MILES' (omitting distanceUnit altogether defaults to 'KM').")
        }

        new LocationsCriteria(locations, distanceUnit as DistanceUnit)
    }

    private static String parseDistanceUnit(Map params) {
        params.distanceUnit ? (params.distanceUnit as String).toUpperCase() : 'KM'
    }

    private static boolean distanceUnitValid(String distanceUnit) {
        distanceUnit.toUpperCase() in DistanceUnit.values().collect { it.toString() }
    }
}
