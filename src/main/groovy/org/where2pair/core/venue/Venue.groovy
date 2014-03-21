package org.where2pair.core.venue

import groovy.transform.AutoClone
import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
@AutoClone
class Venue {
    String id
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

	Distance distanceTo(Coordinates coordinates, DistanceUnit distanceUnit) {
		distanceUnit.distanceBetween(coordinates, location)
	}
}
