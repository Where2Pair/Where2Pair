package org.where2pair

class VenueWriter {

	VenueRepository venueRepository
	
	void save(Venue venue) {
		Venue existingVenue = venueRepository.findByNameAndCoordinates(venue.name, venue.location)
		
		if (existingVenue) {
			venue.id = existingVenue.id
			venueRepository.update(venue)
		} else {
			venueRepository.save(venue)
		}
	}
	
}
