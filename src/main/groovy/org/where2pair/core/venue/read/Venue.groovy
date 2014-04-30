package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.VenueId

@Immutable
@ToString
class Venue {
    VenueId id
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    Set<String> facilities

    static Venue newInstance(VenueId venueId, VenueDetails venueDetails) {
        new Venue(id: venueId,
                name: venueDetails.name,
                location: venueDetails.location,
                address: venueDetails.address,
                weeklyOpeningTimes: venueDetails.weeklyOpeningTimes,
                facilities: venueDetails.facilities)
    }

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