package org.where2pair.read.venue

import static org.where2pair.common.venue.FacilityAvailability.STATUS_UNKNOWN

import groovy.transform.Immutable
import org.where2pair.common.venue.Facility
import org.where2pair.common.venue.FacilityAvailability

@Immutable
class FacilityStatus {

    Facility facility
    FacilityAvailability availability

    static FacilityStatus facilityStatusUnknown(Facility facility) {
        new FacilityStatus(facility, STATUS_UNKNOWN)
    }

    boolean isAvailable() {
        availability.isAvailable()
    }
}
