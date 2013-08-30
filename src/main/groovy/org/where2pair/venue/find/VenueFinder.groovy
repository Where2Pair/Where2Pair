package org.where2pair.venue.find

import org.where2pair.venue.Coordinates;
import org.where2pair.venue.Venue;
import org.where2pair.venue.VenueRepository;

class VenueFinder {

	VenueRepository venueRepository
	DistanceCalculator distanceCalculator
	
	List findNearestTo(OpenTimesCriteria openTimesCriteria, FeaturesCriteria featuresCriteria, Coordinates... coordinates) {
		List openVenues = venueRepository.getAll().findAll { Venue venue -> 
			venue.isOpen(openTimesCriteria) 
		}
		
		List venuesWithFeatures = openVenues.findAll { Venue venue ->
			venue.hasFeatures(featuresCriteria)
		}
		
		List sortedVenues = sortVenuesByDistance(venuesWithFeatures, coordinates)
		
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
