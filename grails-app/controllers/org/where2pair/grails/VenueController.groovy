package org.where2pair.grails

import grails.converters.JSON

import org.springframework.security.access.annotation.Secured
import org.where2pair.Venue
import org.where2pair.VenueRepository

class VenueController {
    VenueRepository venueRepository
    VenueJsonMarshaller venueJsonMarshaller

    def show(long id) {
        Venue venue = venueRepository.get(id)
		
		if (venue) {
	        Map venueJson = venueJsonMarshaller.asVenueJson(venue)
	        render venueJson as JSON
		} else {
			response.status = 404
			render "Venue with id $id could not be found"
		}
    }

    def showAll() {
        List venues = venueRepository.getAll()
        List venuesJson = venueJsonMarshaller.asVenuesJson(venues)
        render venuesJson as JSON
    }

    @Secured(['ROLE_ADMIN'])
    def save() {
		Map json = request.JSON
		Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueRepository.save(venue)
        render json as JSON
    }

}
