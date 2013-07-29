package org.where2pair.grails

import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.VenueFinder

import static java.lang.Double.parseDouble

class VenueController {

	VenueFinder venueFinder
	GormVenueRepository gormVenueRepository
	VenueConverter venueConverter
		
    def show() { 
		List venues = gormVenueRepository.getAll()
		List venueDTOs = asVenueDTOs(venues)
		render venueDTOs as JSON
	}
	
	def findNearest() {
		List coordinates = parseCoordinatesFromRequest()
		
		if (coordinates.size() <= 1000) {
			List venues = venueFinder.findNearestTo(*coordinates)
			List venueWithDistanceDTOs = asVenueWithDistanceDTOs(venues)
			render venueWithDistanceDTOs as JSON
		} else {
			response.status = 413
			render "Only upto 1000 locations are supported at this time."
		}
	}
	
	private List parseCoordinatesFromRequest() {
		params.findAll { it.key.startsWith('location') }.collect {
			def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
			new Coordinates(lat, lng)
		}
	}
	
	def save() {
		VenueDTO venueDTO = new VenueDTO(request.JSON)
		gormVenueRepository.save(venueDTO)
		render new JSON(venueDTO)
	}
	
	private List asVenueDTOs(List venues) {
		venueConverter.asVenueDTOs(venues)
	}
	
	private List asVenueWithDistanceDTOs(List venues) {
		venueConverter.asVenueWithDistanceDTOs(venues)
	}
}
