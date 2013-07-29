package org.where2pair

class VenueFinder {

	VenueRepository venueRepository
	TimeProvider timeProvider
	DistanceCalculator distanceCalculator
	
	List findNearestTo(Coordinates... coordinates) {
		List openVenues = venueRepository.getAll().findAll { Venue venue -> 
			venue.isOpen(timeProvider.currentTime) 
		}
		
		List sortedVenues = sortVenuesByDistance(openVenues, coordinates)
		
		restrictTo50Results(sortedVenues)
	}

	private List sortVenuesByDistance(List openVenues, Coordinates... coordinates) {
		List venuesWithDistance = openVenues.collect { Venue venue -> 
			new VenueWithDistance(venue: venue, distanceInKm: distanceCalculator.distanceInKmTo(venue, coordinates))
		}
		
		venuesWithDistance.sort { VenueWithDistance venue -> 
			venue.distanceInKm 
		}
	}

	private List restrictTo50Results(List venues) {
		venues.size() > 50 ? venues[0..49] : venues
	}
	
}
