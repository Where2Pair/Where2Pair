package org.where2pair.read.venue

import org.where2pair.common.venue.Facility
import org.where2pair.read.venue.find.FacilitiesCriteria

import static org.where2pair.common.venue.Facility.parseFacility


class FacilitiesCriteriaParser {

    FacilitiesCriteria parse(Map<String, ?> params) {
        def requestedFacilitiesAsStrings = (params.withFacilities ? params.withFacilities.split(',') : []) as HashSet<String>
        try {
            def requestedFacilities = requestedFacilitiesAsStrings.collect { parseFacility(it) } as HashSet<Facility>
            new FacilitiesCriteria(requestedFacilities)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("Unrecognized facility requested. Facilities should be comma-separated values from the following list: ${Facility.asStrings()}")
        }
    }
}
