package org.where2pair

import org.joda.time.DateTime

class Venue {

    String name
	Coordinates location
	WeeklyOpeningTimes weeklyOpeningTimes
	
	boolean isOpen(OpenTimesCriteria openTimesCriteria) {
		weeklyOpeningTimes.isOpen(openTimesCriteria)
    }
		
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
