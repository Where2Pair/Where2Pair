package org.where2pair.infra.venue.read

import groovy.transform.TupleConstructor
import org.where2pair.core.venue.common.Facility
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.FacilitiesCriteria
import org.where2pair.core.venue.read.OpenTimesCriteria
import org.where2pair.core.venue.read.VenueService
import org.where2pair.infra.venue.web.JsonResponse

import static DayOfWeek.parseDayOfWeek
import static org.where2pair.core.venue.common.Facility.parseFacility
import static org.where2pair.infra.venue.web.JsonResponse.badRequest
import static org.where2pair.infra.venue.web.JsonResponse.validJsonResponse

@TupleConstructor
class FindVenueController {

    VenueService venueService
    LocationsCriteriaParser locationsCriteriaParser
    TimeProvider timeProvider

    JsonResponse findNearest(Map<String, ?> params) {
        try {
            def openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
            def facilitiesCriteria = parseFacilitiesCriteriaFromRequest(params)
            def locationsCriteria = locationsCriteriaParser.parse(params)
            def venues = venueService.find(openTimesCriteria, facilitiesCriteria, locationsCriteria)
            return validJsonResponse(venues)
        } catch (QueryParseException e) {
            return badRequest(e.message)
        }
    }

    private OpenTimesCriteria parseOpenTimesCriteriaFromRequest(Map<String, ?> params) {
        SimpleTime openFrom = parseOpenFromTimeFromRequest(params)
        SimpleTime openUntil = params.openUntil ? parseSimpleTime(params, 'openUntil') : openFrom

        try {
            DayOfWeek dayOfWeek = params.openDay ? parseDayOfWeek(params.openDay) : timeProvider.today()
            return new OpenTimesCriteria(openFrom: openFrom, openUntil: openUntil, dayOfWeek: dayOfWeek)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("'openDay' not recognized. Expected to be a day from Monday-Sunday")
        }
    }

    private SimpleTime parseOpenFromTimeFromRequest(Map<String, ?> params) {
        params.openFrom ? parseSimpleTime(params, 'openFrom') : timeProvider.timeNow()
    }

    private static SimpleTime parseSimpleTime(Map<String, ?> params, String paramName) {
        String requestParamValue = params[paramName]
        if (!requestParamValue.contains('.')) {
            throw new QueryParseException("'$paramName' not supplied in the correct format. Expected to be in the form: $paramName:<hour>.<minute>")
        }

        def (hour, minute) = requestParamValue.split(/\./)

        try {
            return new SimpleTime(hour as Integer, minute as Integer)
        } catch (NumberFormatException e) {
            throw new QueryParseException("'$paramName' not supplied in the correct format. Expected to be in the form: $paramName:<hour>.<minute>")
        }
    }

    private static FacilitiesCriteria parseFacilitiesCriteriaFromRequest(Map<String, ?> params) {
        def requestedFacilitiesAsStrings = (params.withFacilities ? params.withFacilities.split(',') : []) as HashSet<String>
        try {
            def requestedFacilities = requestedFacilitiesAsStrings.collect { parseFacility(it) } as HashSet<Facility>
            new FacilitiesCriteria(requestedFacilities)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("Unrecognized facility requested. Facilities should be comma-separated values from the following list: ${Facility.values()}")
        }
    }
}
