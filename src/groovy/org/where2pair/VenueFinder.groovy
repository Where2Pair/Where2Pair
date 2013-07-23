package org.where2pair

class VenueFinder {

	VenueRepository venueRepository
	TimeProvider timeProvider
	
	List findNearestTo(Coordinates coordinates) {
		List venues = venueRepository.getAll().findAll { 
			Venue venue -> venue.isOpen(timeProvider.currentTime) 
		}
		
		if (venues.size() > 50)
			return venues[0..49]
		
		venues
	}
	
}
