package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.Facility

@Immutable
class FacilitiesCriteria {
    Set<Facility> requestedFacilities

    static FacilitiesCriteria anyFacilities() {
        new FacilitiesCriteria(requestedFacilities: [])
    }
}
