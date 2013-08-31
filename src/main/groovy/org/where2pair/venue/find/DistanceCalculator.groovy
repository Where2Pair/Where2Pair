package org.where2pair.venue.find

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import static org.where2pair.venue.find.DistanceUnit.METRIC

class DistanceCalculator {

	double distanceBetween(Venue venue, LocationsCriteria locationsCriteria) {
		List locations = locationsCriteria.locations
		
		List distances = locations.collect { 
			locationsCriteria.distanceUnit == METRIC ? venue.distanceInKmTo(it) : venue.distanceInMilesTo(it) 
		}
		
		distances.sum() / distances.size()
	}
	
}
