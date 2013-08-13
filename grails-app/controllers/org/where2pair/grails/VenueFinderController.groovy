package org.where2pair.grails

import static java.lang.Double.parseDouble
import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.DayOfWeek
import org.where2pair.FeaturesCriteria
import org.where2pair.OpenTimesCriteria
import org.where2pair.TimeProvider
import org.where2pair.VenueFinder
import org.where2pair.DailyOpeningTimes.SimpleTime

class VenueFinderController {

	VenueFinder venueFinder
	GormVenueRepository gormVenueRepository
	VenueToJsonConverter venueConverter
	TimeProvider timeProvider
	
	def findNearest() {
		List coordinates = parseCoordinatesFromRequest()
		
		if (coordinates.size() in 1..1000) {
			OpenTimesCriteria openTimesCriteria = parseOpenTimesCriteriaFromRequest()
			FeaturesCriteria featuresCriteria = parseFeaturesCriteriaFromRequest()
			List venues = venueFinder.findNearestTo(openTimesCriteria, featuresCriteria, *coordinates)
			List venuesWithDistanceJson = venueConverter.asVenuesWithDistanceJson(venues)
			render venuesWithDistanceJson as JSON
		} else {
			handleIllegalCoordinatesCount(coordinates)
		}
	}
	
	private List parseCoordinatesFromRequest() {
		params.findAll { it.key.startsWith('location') }.collect {
			def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
			new Coordinates(lat, lng)
		}
	}
	
	private OpenTimesCriteria parseOpenTimesCriteriaFromRequest() {
		SimpleTime openFrom = parseOpenFromTimeFromRequest()
		SimpleTime openUntil = params.openUntil ? parseSimpleTime(params.openUntil) : openFrom
		DayOfWeek dayOfWeek = params.openDay ? DayOfWeek.parseString(params.openDay) : timeProvider.today()
		new OpenTimesCriteria(openFrom: openFrom, openUntil: openUntil, dayOfWeek: dayOfWeek)
	}
	
	private SimpleTime parseOpenFromTimeFromRequest() {
		params.openFrom ? parseSimpleTime(params.openFrom) : timeProvider.timeNow()
	}
	
	private SimpleTime parseSimpleTime(String requestParam) {
		def (hour, minute) = requestParam.split(/\./)
		return new SimpleTime(hour as Integer, minute as Integer)
	}
	
	private FeaturesCriteria parseFeaturesCriteriaFromRequest() {
		Set requestedFeatures = params.withFeatures ? params.withFeatures.split(',') : []
		new FeaturesCriteria(requestedFeatures: requestedFeatures)
	}
	
	private void handleIllegalCoordinatesCount(List coordinates) {
		response.status = 413
				
		if (!coordinates)
			render "Missing locations from the request parameters. I expect a query in the form: findNearest?location1=x1,y1&location2=x2,y2..."
		else
			render "Only upto 1000 locations are supported at this time."
	}
}
