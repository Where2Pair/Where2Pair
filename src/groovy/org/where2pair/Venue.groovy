package org.where2pair

import org.joda.time.DateTime

class Venue {

	Coordinates location
	WeeklyOpeningTimes weeklyOpeningTimes
	
	boolean isOpen(DateTime dateTime) {
		weeklyOpeningTimes.isOpen(dateTime)
	}
	
	double distanceInKmTo(Coordinates coordinates) {
		double earthRadius = 6371
		double dLat = Math.toRadians(coordinates.lat-location.lat)
		double dLng = Math.toRadians(coordinates.lng-location.lng)
		double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
				   Math.cos(Math.toRadians(coordinates.lat)) * Math.cos(Math.toRadians(location.lat)) *
				   Math.sin(dLng/2) * Math.sin(dLng/2);
		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a))
		double dist = earthRadius * c
		dist.doubleValue()
	}
}
