package org.where2pair

class Venue {
    long id
    String name
	Coordinates location
	Address address
	WeeklyOpeningTimes weeklyOpeningTimes
	Set features
	
	boolean isOpen(OpenTimesCriteria openTimesCriteria) {
		weeklyOpeningTimes.isOpen(openTimesCriteria)
    }
		
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
