package org.where2pair.grails

import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.VenueFinder

import static java.lang.Double.parseDouble

class VenueFinderController {

	VenueFinder venueFinder
	GormVenueRepository gormVenueRepository
	VenueConverter venueConverter
	
	def findNearest() {
		List coordinates = parseCoordinatesFromRequest()
		
		if (coordinates.size() in 1..1000) {
			List venues = venueFinder.findNearestTo(*coordinates)
			List venueWithDistanceDTOs = asVenueWithDistanceDTOs(venues)
			render venueWithDistanceDTOs as JSON
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
	
	private List asVenueWithDistanceDTOs(List venues) {
		venueConverter.asVenueWithDistanceDTOs(venues)
	}
	
	private void handleIllegalCoordinatesCount(List coordinates) {
		response.status = 413
				
		if (!coordinates)
			render "Missing locations from the request parameters. I expect a query in the form: findNearest?location1=x1,y1&location2=x2,y2..."
		else
			render "Only upto 1000 locations are supported at this time."
	}
}
