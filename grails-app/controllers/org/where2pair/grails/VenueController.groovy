package org.where2pair.grails

import grails.converters.JSON
import org.springframework.security.access.annotation.Secured
import org.where2pair.Venue

class VenueController {
    GormVenueRepository gormVenueRepository
    VenueToJsonConverter venueConverter

    def show(long id) {
        Venue venue = gormVenueRepository.get(id)
		
		if (venue) {
	        Map venueJson = venueConverter.asVenueJson(venue)
	        render venueJson as JSON
		} else {
			response.status = 404
			render "Venue with id $id could not be found"
		}
    }

    @Secured(['ROLE_ADMIN'])
    def showAll() {
        List venues = gormVenueRepository.getAll()
        List venuesJson = venueConverter.asVenuesJson(venues)
        render venuesJson as JSON
    }

    @Secured(['ROLE_ADMIN'])
    def save() {
        VenueDto venueDto = new VenueDto(request.JSON)
        long id = gormVenueRepository.save(venueDto)
        venueDto.id = id
        render new JSON(venueDto)
    }

}
