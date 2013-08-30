package org.where2pair.venue


class VenueController {
    VenueRepository venueRepository
	VenueSaveOrUpdater venueSaveOrUpdater
    VenueJsonMarshaller venueJsonMarshaller

    def show(long id) {
        Venue venue = venueRepository.get(id)
		
		if (venue) {
	        Map venueJson = venueJsonMarshaller.asVenueJson(venue)
	        return venueJson
		} else {
			return new ErrorResponse(message: "Venue with id $id could not be found", status: 404)
		}
    }

    def showAll() {
        List venues = venueRepository.getAll()
        List venuesJson = venueJsonMarshaller.asVenuesJson(venues)
		venuesJson
    }

    def save(Map json) {
		Venue venue = venueJsonMarshaller.asVenue(json)
        json.id = venueSaveOrUpdater.save(venue)
        json
    }

}
