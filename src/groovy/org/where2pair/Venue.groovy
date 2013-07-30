package org.where2pair

import org.joda.time.DateTime

class Venue {

	Coordinates location
	WeeklyOpeningTimes weeklyOpeningTimes
    String name
	
	boolean isOpen(OpenTimesCriteria openTimesCriteria) {
		weeklyOpeningTimes.isOpen(openTimesCriteria)
	}
	
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
