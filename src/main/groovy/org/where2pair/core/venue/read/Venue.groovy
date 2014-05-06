package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.Facility
import org.where2pair.core.venue.common.VenueId

import static org.where2pair.core.venue.read.FacilityStatus.AVAILABLE

@Immutable
@ToString
class Venue {
    VenueId id
    String name
    Coordinates location
    Address address
    WeeklyOpeningTimes weeklyOpeningTimes
    FacilityStatuses availableFacilities

    //TODO when writing new venues, availableFacilities should be a map
    //WIFI: No, Mobile payments: YES. Otherwise STATUS_UNKNOWN
    //The above variable could be of type Facilities

    static Venue newInstance(VenueId venueId, VenueDetails venueDetails) {
        new Venue(id: venueId,
                name: venueDetails.name,
                location: venueDetails.location,
                address: venueDetails.address,
                weeklyOpeningTimes: venueDetails.weeklyOpeningTimes,
                availableFacilities: venueDetails.availableFacilities)
    }

    boolean isOpen(OpenTimesCriteria openTimesCriteria) {
        weeklyOpeningTimes.isOpen(openTimesCriteria)
    }

    boolean hasFacilities(FacilitiesCriteria facilitiesCriteria) {
        Set<Facility> availableFacilities = availableFacilities.findAll { it.value == AVAILABLE }.keySet()
        facilitiesCriteria.requestedFacilities.every { it in availableFacilities }
    }

    Distance distanceTo(Coordinates coordinates, DistanceUnit distanceUnit) {
        distanceUnit.distanceBetween(coordinates, location)
    }
}
