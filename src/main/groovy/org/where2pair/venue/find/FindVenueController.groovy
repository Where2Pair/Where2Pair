package org.where2pair.venue.find

import static org.where2pair.venue.DayOfWeek.parseDayOfWeek

import org.where2pair.venue.DayOfWeek
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.DailyOpeningTimes.SimpleTime

class FindVenueController {

	VenueFinder venueFinder
	LocationsCriteriaParser locationsCriteriaParser
	VenueJsonMarshaller venueJsonMarshaller
	TimeProvider timeProvider
	
	def findNearest(Map params) {
		LocationsCriteria locationsCriteria = locationsCriteriaParser.parse(params)
		
		if (!locationsCriteria.errors) {
			OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
			FeaturesCriteria featuresCriteria = parseFeaturesCriteriaFromRequest(params)
			List venues = venueFinder.findNearestTo(openTimesCriteria, featuresCriteria, locationsCriteria)
			return venueJsonMarshaller.asVenuesWithDistanceJson(venues)
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
	
	private FeaturesCriteria parseFeaturesCriteriaFromRequest(Map params) {
		Set requestedFeatures = params.withFeatures ? params.withFeatures.split(',') : []
		new FeaturesCriteria(requestedFeatures: requestedFeatures)
	}
	
	private ErrorResponse handleIllegalLocationsCriteria(LocationsCriteria suppliedLocations) {
		def (errorMessage, status) = suppliedLocations.errors
		new ErrorResponse(message: errorMessage, status: status)
	}
}
