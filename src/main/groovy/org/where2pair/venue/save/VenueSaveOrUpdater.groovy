package org.where2pair.venue.save

import org.where2pair.venue.Venue;
import org.where2pair.venue.VenueRepository;


class VenueSaveOrUpdater {

	VenueRepository venueRepository
	
	long save(Venue venue) {
		Venue existingVenue = venueRepository.findByNameAndCoordinates(venue.name, venue.location)
		
		if (existingVenue) {
			venue.id = existingVenue.id
			venueRepository.update(venue)
			return venue.id
		} else {
			return venueRepository.save(venue)
		}
	}
	
}
