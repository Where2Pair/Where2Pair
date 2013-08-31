package org.where2pair.venue.find

enum DistanceUnit {

	KM, MILES
	
	static DistanceUnit parseDistanceUnit(String distanceUnit) {
		distanceUnit.toUpperCase()
	}
	
}
