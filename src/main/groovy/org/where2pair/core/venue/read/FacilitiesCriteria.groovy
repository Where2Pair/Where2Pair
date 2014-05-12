package org.where2pair.core.venue.read

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.core.venue.common.Facility

@Immutable
@ToString
class FacilitiesCriteria {
    Set<Facility> requestedFacilities

    static FacilitiesCriteria anyFacilities() {
        new FacilitiesCriteria(requestedFacilities: [])
    }
}
