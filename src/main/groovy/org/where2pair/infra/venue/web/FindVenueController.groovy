package org.where2pair.infra.venue.web

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.FacilitiesCriteria
import org.where2pair.core.venue.read.LocationsCriteria
import org.where2pair.core.venue.read.OpenTimesCriteria
import org.where2pair.core.venue.read.TimeProvider
import org.where2pair.core.venue.read.VenueService

import static DayOfWeek.parseDayOfWeek

class FindVenueController {

    VenueService venueFinder
    LocationsCriteriaParser locationsCriteriaParser
    VenueJsonMarshaller venueJsonMarshaller
    TimeProvider timeProvider

    def findNearest(Map params) {
        LocationsCriteria locationsCriteria = locationsCriteriaParser.parse(params)

        if (!locationsCriteria.errors) {
            OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
            FacilitiesCriteria facilitiesCriteria = parseFacilitiesCriteriaFromRequest(params)
            List venues = venueFinder.findNearestTo(openTimesCriteria, facilitiesCriteria, locationsCriteria)
            return venueJsonMarshaller.asVenuesWithDistancesJson(venues)
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

    private ErrorResponse handleIllegalLocationsCriteria(LocationsCriteria suppliedLocations) {
        def (errorMessage, status) = suppliedLocations.errors
        new ErrorResponse(message: errorMessage, status: status)
    }
}
