package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.Venue

@EqualsAndHashCode
@ToString
class LocationsCriteria {

    final static VALID_DISTANCE_UNITS = ['MILES', 'KM']
    Map locations
    String distanceUnit

    void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit.toUpperCase()
    }

    int size() {
        locations.size()
    }

    Map distancesTo(Venue venue) {
        locations.collectEntries { label, coordinates ->
            [(label): distanceUnit == 'KM' ? venue.distanceInKmTo(coordinates) : venue.distanceInMilesTo(coordinates)]
        }
    }

    def getErrors() {
        if (isValid()) return null

        String errorMessage
        int status

        if (locations.size() == 0) {
            errorMessage = "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
            status = 400
        } else if (locations.size() > 100) {
            errorMessage = "Only upto 1000 locations are supported at this time."
            status = 413
        } else {
            errorMessage = "Distance unit '${this.@distanceUnit}' is invalid. Use either 'km' or 'miles' (omitting distanceUnit altogether defaults to 'km')."
            status = 400
        }

        [errorMessage, status]
    }

    private boolean isValid() {
        locations.size() in 1..1000 && distanceUnit in VALID_DISTANCE_UNITS
    }
}
