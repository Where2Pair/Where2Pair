package org.where2pair.grails

import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.VenueFinder

import static java.lang.Double.parseDouble

class VenueController {

	GormVenueRepository gormVenueRepository
	VenueConverter venueConverter
		
    def show() { 
		List venues = gormVenueRepository.getAll()
		List venueDTOs = asVenueDTOs(venues)
		render venueDTOs as JSON
	}
	
	def save() {
		VenueDTO venueDTO = new VenueDTO(request.JSON)
		gormVenueRepository.save(venueDTO)
		render new JSON(venueDTO)
	}
	
	private List asVenueDTOs(List venues) {
		venueConverter.asVenueDTOs(venues)
	}

}
