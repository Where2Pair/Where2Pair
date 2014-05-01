package org.where2pair.infra.venue.read

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.*

import static DayOfWeek.parseDayOfWeek

class FindVenueController {

    VenueService venueFinder
    LocationsCriteriaParser locationsCriteriaParser
    TimeProvider timeProvider

    def findNearest(Map params) {
        LocationsCriteria locationsCriteria = locationsCriteriaParser.parse(params)

        if (!locationsCriteria.errors) {
            OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
            FacilitiesCriteria facilitiesCriteria = parseFacilitiesCriteriaFromRequest(params)
            List venues = venueFinder.find(openTimesCriteria, facilitiesCriteria, locationsCriteria)
            return null
        } else {
            return handleIllegalLocationsCriteria(locationsCriteria)
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
        Set requestedFacilities = params.withFacilities ? params.withFacilities.split(',') : []
        new FacilitiesCriteria(requestedFacilities: requestedFacilities)
    }

    private def handleIllegalLocationsCriteria(LocationsCriteria suppliedLocations) {
        def (errorMessage, status) = suppliedLocations.errors
        null
    }
}
