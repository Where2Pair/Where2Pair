package org.where2pair.read.venue

import static FacilityStatus.facilityStatusUnknown

import groovy.transform.Immutable
import org.where2pair.common.venue.Facility

@Immutable
class FacilityStatuses {
    Set<FacilityStatus> facilityStatuses

    static FacilityStatuses facilityStatusesFor(Set<FacilityStatus> facilityStatuses) {
        Set<Facility> unknownStatusFacilities = Facility.values() - facilityStatuses.facility
        Set<FacilityStatus> unknownStatusFacilityStatuses = unknownStatusFacilities.collect {
            facilityStatusUnknown(it)
        }

        new FacilityStatuses(facilityStatuses: facilityStatuses + unknownStatusFacilityStatuses)
    }

    boolean hasFacilities(FacilitiesCriteria facilitiesCriteria) {
        def statusesForRequestFacilities = facilityStatuses.findAll {
            it.facility in facilitiesCriteria.requestedFacilities
        }
        statusesForRequestFacilities.every { it.available }
    }
}

