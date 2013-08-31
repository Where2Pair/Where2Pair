package org.where2pair.venue.find

import static java.lang.Double.parseDouble

import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.venue.Coordinates;
import org.where2pair.venue.DayOfWeek;
import org.where2pair.venue.ErrorResponse;
import org.where2pair.venue.VenueJsonMarshaller;

import static org.where2pair.venue.find.DistanceUnit.METRIC

class FindVenueController {

	VenueFinder venueFinder
	VenueJsonMarshaller venueJsonMarshaller
	TimeProvider timeProvider
	
	def findNearest(Map params) {
		LocationsCriteria locationsCriteria = parseLocationsCriteriaFromRequest(params)
		
		if (locationsCriteria.size() in 1..1000) {
			OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest(params)
			FeaturesCriteria featuresCriteria = parseFeaturesCriteriaFromRequest(params)
			List venues = venueFinder.findNearestTo(openTimesCriteria, featuresCriteria, locationsCriteria)
			return venueJsonMarshaller.asVenuesWithDistanceJson(venues)
		} else {
			return handleIllegalLocationsCount(locationsCriteria)
		}
	}
	
	private LocationsCriteria parseLocationsCriteriaFromRequest(Map params) {
		List locations = params.findAll { it.key.startsWith('location') }.collect {
			def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
			new Coordinates(lat, lng)
		}
		
		def distanceUnit = params.distanceUnit ? DistanceUnit.parseString(params.distanceUnit) : METRIC
		
		new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
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
	
	private ErrorResponse handleIllegalLocationsCount(LocationsCriteria suppliedLocations) {
		String errorMessage = suppliedLocations.size() == 0 ? 
			"Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
			: "Only upto 1000 locations are supported at this time."
			
		new ErrorResponse(message: errorMessage, status: 413)
	}
}
