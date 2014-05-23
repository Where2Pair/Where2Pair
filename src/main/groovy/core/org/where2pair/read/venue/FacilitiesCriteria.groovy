package org.where2pair.read.venue

import groovy.transform.Immutable
import groovy.transform.ToString
import org.where2pair.common.venue.Facility

@Immutable
@ToString
class FacilitiesCriteria {
    Set<Facility> requestedFacilities

}

