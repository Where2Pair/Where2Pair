package org.where2pair.venue.find

enum DistanceUnit {

	METRIC, IMPERIAL
	
	static DistanceUnit parseDistanceUnit(String distanceUnit) {
		distanceUnit.toUpperCase()
	}
	
}
