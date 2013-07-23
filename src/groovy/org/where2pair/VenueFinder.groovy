package org.where2pair

class VenueFinder {

	VenueRepository venueRepository
	TimeProvider timeProvider
	
	List findNearestTo(Coordinates coordinates) {
		List openVenues = venueRepository.getAll().findAll { Venue venue -> 
			venue.isOpen(timeProvider.currentTime) 
		}
		
		List cappedVenueResults = restrictTo50Results(openVenues)
		
		sortVenuesIntoAscendingOrderByDistance(cappedVenueResults, coordinates)
	}

	private List sortVenuesIntoAscendingOrderByDistance(List openVenues, Coordinates coordinates) {
		openVenues.sort { Venue venue -> venue.distanceInKmTo(coordinates) }
	}

	private List restrictTo50Results(List venues) {
		if (venues.size() > 50)
			return venues[0..49]
		
		venues
	}
	
}
