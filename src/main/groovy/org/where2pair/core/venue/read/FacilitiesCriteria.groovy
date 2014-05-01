package org.where2pair.core.venue.read

import groovy.transform.Immutable

@Immutable
class FacilitiesCriteria {
    Set<Facility> requestedFacilities

    static FacilitiesCriteria anyFacilities() {
        new FacilitiesCriteria(requestedFacilities: [])
    }
}
