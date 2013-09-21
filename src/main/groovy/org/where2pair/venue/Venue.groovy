package org.where2pair.venue

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString
import org.where2pair.venue.find.FacilitiesCriteria
import org.where2pair.venue.find.OpenTimesCriteria

@EqualsAndHashCode
@ToString
@AutoClone
class Venue {
    long id
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    Set facilities

    boolean isOpen(OpenTimesCriteria openTimesCriteria) {
        weeklyOpeningTimes.isOpen(openTimesCriteria)
    }

    boolean hasFacilities(FacilitiesCriteria facilitiesCriteria) {
        Set upperCaseAvailableFacilities = facilities.collect { it.toUpperCase() }
        Set upperCaseRequestedFacilities = facilitiesCriteria.requestedFacilities.collect { it.toUpperCase() }
        upperCaseRequestedFacilities.every { it in upperCaseAvailableFacilities }
    }

    double distanceInKmTo(Coordinates coordinates) {
        location.distanceInKmTo(coordinates)
    }

    double distanceInMilesTo(Coordinates coordinates) {
        location.distanceInMilesTo(coordinates)
    }
}
