package org.where2pair

class DistanceCalculator {

	double distanceInKmTo(Venue venue, Coordinates... coordinates) {
		coordinates.collect { venue.distanceInKmTo(it) }.sum() / coordinates.size()
	}
	
}
