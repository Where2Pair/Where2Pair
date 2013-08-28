package org.where2pair

import org.where2pair.Venue
import org.where2pair.VenueRepository
import org.where2pair.VenueWriter

class VenueController {
    VenueRepository venueRepository
	VenueWriter venueWriter
    VenueJsonMarshaller venueJsonMarshaller

    def show(long id) {
        Venue venue = venueRepository.get(id)
		
		if (venue) {
	        Map venueJson = venueJsonMarshaller.asVenueJson(venue)
	        return venueJson
		} else {
			return [:]	
//			response.status = 404
//			render "Venue with id $id could not be found"
		}
    }

    def showAll() {
        List venues = venueRepository.getAll()
        List venuesJson = venueJsonMarshaller.asVenuesJson(venues)
		venuesJson
    }

    def save() {
		Map json = request.JSON
		Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueWriter.save(venue)
        json
    }

}
