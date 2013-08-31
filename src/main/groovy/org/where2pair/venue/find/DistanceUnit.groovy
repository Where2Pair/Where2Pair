package org.where2pair.venue.find

enum DistanceUnit {

	METRIC, IMPERIAL
	
	static DistanceUnit parseString(String distanceUnit) {
		distanceUnit.toUpperCase()
	}
	
}
