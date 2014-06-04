package org.where2pair.read.venue.find

import static org.where2pair.common.venue.Facility.parseFacility

import org.where2pair.common.venue.Facility

class FacilitiesCriteriaParser {

    FacilitiesCriteria parse(Map<String, ?> params) {
        def requestedFacilitiesAsStrings = (params.withFacilities ?
                params.withFacilities.split(',') : []) as Set<String>

        try {
            def requestedFacilities = requestedFacilitiesAsStrings.collect { parseFacility(it) } as Set<Facility>
            new FacilitiesCriteria(requestedFacilities)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("Unrecognized facility requested. Facilities should be comma-separated " +
                    "values from the following list: ${Facility.asStrings()}")
        }
    }
}

