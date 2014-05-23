package org.where2pair.read.venue

import static java.lang.Double.parseDouble

import org.where2pair.common.venue.Coordinates

class LocationsCriteriaParser {

    LocationsCriteria parse(Map<String, ?> params) {
        List<String> locationParams = params.location ?: []
        List locations = locationParams?.collect {
            def (lat, lng) = it.split(',').collect { parseDouble(it) }
            new Coordinates(lat, lng)
        }

        if (locations.size() == 0) {
            throw new QueryParseException('Missing locations from request parameters. Query expected to be in ' +
                    'the form: nearest?location1=x1,y1&location2=x2,y2...')
        }

        def distanceUnit = parseDistanceUnit(params)

        if (!distanceUnitValid(distanceUnit)) {
            throw new QueryParseException("Distance unit '$distanceUnit' is invalid. Use either 'KM' or 'MILES' " +
                    "(omitting distanceUnit altogether defaults to 'KM').")
        }

        new LocationsCriteria(locations, distanceUnit as DistanceUnit)
    }

    private static String parseDistanceUnit(Map<String, ?> params) {
        params.distanceUnit ? (params.distanceUnit as String).toUpperCase() : 'KM'
    }

    private static boolean distanceUnitValid(String distanceUnit) {
        distanceUnit.toUpperCase() in DistanceUnit.values()*.toString()
    }
}

