package org.where2pair.read.venue.find

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.read.venue.Facility

@Immutable
@ToString
class FacilitiesCriteria {
    Set<Facility> requestedFacilities

    static FacilitiesCriteria anyFacilities() {
        new FacilitiesCriteria(requestedFacilities: [])
    }
}
