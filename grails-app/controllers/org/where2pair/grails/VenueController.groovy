package org.where2pair.grails

import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.VenueFinder

import static java.lang.Double.parseDouble

class VenueController {

	VenueFinder venueFinder
	GormVenueRepository gormVenueRepository
		
    def show() { 
		List venues = gormVenueRepository.getAll()
		render venues as JSON
	}
	
	def findNearest() {
		def (lat, lng) = params.'location1'.split(',').collect { parseDouble(it) }
		List venues = venueFinder.findNearestTo(new Coordinates(lat: lat, lng: lng))
		render venues as JSON
	}
	
	def save() {
		VenueDTO venueDTO = new VenueDTO(request.JSON)
		gormVenueRepository.save(venueDTO)
		render new JSON(venueDTO)
	}
}
