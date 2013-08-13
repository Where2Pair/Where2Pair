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
		
	boolean hasFeatures(FeaturesCriteria featuresCriteria) {
		featuresCriteria.requestedFeatures.every { it in features }
	}
	
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
