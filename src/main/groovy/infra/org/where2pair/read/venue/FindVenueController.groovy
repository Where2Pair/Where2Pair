package org.where2pair.read.venue

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.Facility
import org.where2pair.common.venue.JsonResponse
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.find.FacilitiesCriteria
import org.where2pair.read.venue.find.OpenTimesCriteria
import org.where2pair.read.venue.find.OpenTimesCriteriaFactory
import org.where2pair.read.venue.mappingtojson.VenuesWithDistancesToJsonMapper

import static DayOfWeek.parseDayOfWeek
import static org.where2pair.common.venue.JsonResponse.badRequest
import static org.where2pair.common.venue.JsonResponse.validJsonResponse
import static org.where2pair.common.venue.Facility.parseFacility

@TupleConstructor
class FindVenueController {

    VenueService venueService
    LocationsCriteriaParser locationsCriteriaParser
    OpenTimesCriteriaFactory openTimesCriteriaFactory

    JsonResponse findNearest(Map<String, ?> params) {
        try {
            def openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
            def facilitiesCriteria = parseFacilitiesCriteriaFromRequest(params)
            def locationsCriteria = locationsCriteriaParser.parse(params)

            def venues = venueService.find(openTimesCriteria, facilitiesCriteria, locationsCriteria)

            def venuesJson = new VenuesWithDistancesToJsonMapper().toJson(venues)
            return validJsonResponse(venuesJson)
        } catch (QueryParseException e) {
            return badRequest(e.message)
        }
    }

    private OpenTimesCriteria parseOpenTimesCriteriaFromRequest(Map<String, ?> params) {
        SimpleTime openFrom = parseOpenFromTimeFromRequest(params)
        SimpleTime openUntil = params.openUntil ? parseSimpleTime(params, 'openUntil') : openFrom

        try {
            DayOfWeek dayOfWeek = params.openDay ? parseDayOfWeek(params.openDay) : null
            return openTimesCriteriaFactory.createOpenTimesCriteria(openFrom, openUntil, dayOfWeek)
        } catch (IllegalArgumentException e) {
            throw new QueryParseException("'openDay' not recognized. Expected to be a day from Monday-Sunday")
        }
    }

    private SimpleTime parseOpenFromTimeFromRequest(Map<String, ?> params) {
        params.openFrom ? parseSimpleTime(params, 'openFrom') : null
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
            throw new QueryParseException("Unrecognized facility requested. Facilities should be comma-separated values from the following list: ${Facility.asStrings()}")
        }
    }
}
