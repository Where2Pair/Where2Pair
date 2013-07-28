package org.where2pair

import org.joda.time.DateTime

class Venue {

	Coordinates location
	WeeklyOpeningTimes weeklyOpeningTimes
	
	boolean isOpen(DateTime dateTime) {
		weeklyOpeningTimes.isOpen(dateTime)
	}
	
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
