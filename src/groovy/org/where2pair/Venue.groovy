package org.where2pair

import org.joda.time.DateTime

class Venue {

	Coordinates location
	WeeklyOpenTimes weeklyOpenTimes
	
	//How to model open hours? -> Perhaps a nice little library here would be useful
	//Can we have a list of durations?
	//Simple approach would be a list of Periods, where period contained start and end time in hours and minutes
	//DailyOpenTimes would contain a list of OpenTimes
	//WeeklyOpenTimes would map days to DailyOpenTimes
	// -> weeklyOpenTimes.isOpen(dateTime)
	// 	-> int day = dateTime.getDay()
	//	-> dailyOpenTimes.isOpen(dateTime)
	
	boolean isOpen(DateTime dateTime) {
		weeklyOpenTimes.isOpen(dateTime)
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
