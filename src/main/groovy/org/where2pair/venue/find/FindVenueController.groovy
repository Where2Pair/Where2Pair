package org.where2pair.venue.find

import static java.lang.Double.parseDouble

import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.venue.Coordinates;
import org.where2pair.venue.DayOfWeek;
import org.where2pair.venue.ErrorResponse;
import org.where2pair.venue.VenueJsonMarshaller;

class FindVenueController {

	VenueFinder venueFinder
	VenueJsonMarshaller venueJsonMarshaller
	TimeProvider timeProvider
	
	def findNearest(Map params) {
		List coordinates = parseCoordinatesFromRequest(params)
		
		if (coordinates.size() in 1..1000) {
			OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
			FeaturesCriteria featuresCriteria = parseFeaturesCriteriaFromRequest(params)
			List venues = venueFinder.findNearestTo(openTimesCriteria, featuresCriteria, *coordinates)
			List venuesWithDistanceJson = venueJsonMarshaller.asVenuesWithDistanceJson(venues)
			return venuesWithDistanceJson
		} else {
			return handleIllegalCoordinatesCount(coordinates)
		}
	}
	
	private List parseCoordinatesFromRequest(Map params) {
		params.findAll { it.key.startsWith('location') }.collect {
			def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
			new Coordinates(lat, lng)
		}
	}
	
	private OpenTimesCriteria parseOpenTimesCriteriaFromRequest(Map params) {
		SimpleTime openFrom = parseOpenFromTimeFromRequest(params)
		SimpleTime openUntil = params.openUntil ? parseSimpleTime(params.openUntil) : openFrom
		DayOfWeek dayOfWeek = params.openDay ? DayOfWeek.parseString(params.openDay) : timeProvider.today()
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
	
	private ErrorResponse handleIllegalCoordinatesCount(List suppliedCoordinates) {
		String errorMessage = !suppliedCoordinates ? "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
			: "Only upto 1000 locations are supported at this time."
			
		new ErrorResponse(message: errorMessage, status: 413)
	}
}
