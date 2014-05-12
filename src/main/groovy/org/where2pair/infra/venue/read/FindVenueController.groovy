package org.where2pair.infra.venue.read

import groovy.json.JsonOutput
import groovy.transform.TupleConstructor
import org.where2pair.core.venue.common.Facility
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.*
import org.where2pair.infra.venue.web.JsonResponse

import static DayOfWeek.parseDayOfWeek
import static groovy.json.JsonOutput.toJson
import static org.where2pair.infra.venue.web.JsonResponse.badRequest
import static org.where2pair.infra.venue.web.JsonResponse.validJsonResponse

@TupleConstructor
class FindVenueController {

    VenueService venueService
    LocationsCriteriaParser locationsCriteriaParser
    TimeProvider timeProvider

    JsonResponse findNearest(Map params) {
        try {
            def openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
            def facilitiesCriteria = parseFacilitiesCriteriaFromRequest(params)
            def locationsCriteria = locationsCriteriaParser.parse(params)
            List venues = venueService.find(openTimesCriteria, facilitiesCriteria, locationsCriteria)
            return validJsonResponse(venues)
        } catch (QueryParseException e) {
            return badRequest(e.message)
        }
    }

    private OpenTimesCriteria parseOpenTimesCriteriaFromRequest(Map params) {
        SimpleTime openFrom = parseOpenFromTimeFromRequest(params)
        SimpleTime openUntil = params.openUntil ? parseSimpleTime(params.openUntil) : openFrom
        DayOfWeek dayOfWeek = params.openDay ? parseDayOfWeek(params.openDay) : timeProvider.today()
        new OpenTimesCriteria(openFrom: openFrom, openUntil: openUntil, dayOfWeek: dayOfWeek)
    }

    private SimpleTime parseOpenFromTimeFromRequest(Map params) {
        params.openFrom ? parseSimpleTime(params.openFrom) : timeProvider.timeNow()
    }

    private SimpleTime parseSimpleTime(String requestParam) {
        def (hour, minute) = requestParam.split(/\./)
        return new SimpleTime(hour as Integer, minute as Integer)
    }

    private FacilitiesCriteria parseFacilitiesCriteriaFromRequest(Map params) {
        Set<String> requestedFacilities = params.withFacilities ? params.withFacilities.split(',') : []
        new FacilitiesCriteria(requestedFacilities: requestedFacilities.collect { it.toUpperCase() as Facility })
    }
}
