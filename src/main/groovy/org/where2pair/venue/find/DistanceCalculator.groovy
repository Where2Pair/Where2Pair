package org.where2pair.venue.find

import org.where2pair.venue.Coordinates;
import org.where2pair.venue.Venue;

class DistanceCalculator {

	double distanceInKmTo(Venue venue, Coordinates... coordinates) {
		coordinates.collect { venue.distanceInKmTo(it) }.sum() / coordinates.size()
	}
	
}
