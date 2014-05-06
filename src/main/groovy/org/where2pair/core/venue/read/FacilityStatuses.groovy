package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Facility

import static org.where2pair.core.venue.read.FacilityStatus.facilityAvailable
import static org.where2pair.core.venue.read.FacilityStatus.facilityUnavailable

@Immutable
class FacilityStatuses {
    @Delegate private List<FacilityStatus> facilityStatuses

    static FacilityStatuses statusesFor(Collection<Facility> availableFacilities) {
        Collection<Facility> unavailableFacilities = Facility.values() - availableFacilities

        List<FacilityStatus> available = availableFacilities.collect { facilityAvailable(it) }
        List<FacilityStatus> unavailable = unavailableFacilities.collect { facilityUnavailable(it) }

        new FacilityStatuses(facilityStatuses: (available + unavailable))
    }

}
