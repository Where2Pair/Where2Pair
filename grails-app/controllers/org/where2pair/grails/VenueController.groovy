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
		def (lat, lng) = params.'location1'.split(',').collect { parseDouble(it) }
		List venues = venueFinder.findNearestTo(new Coordinates(lat: lat, lng: lng))
		List venueWithDistanceDTOs = asVenueWithDistanceDTOs(venues)
		render venueWithDistanceDTOs as JSON
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
