package org.where2pair

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
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
		Set upperCaseAvailableFeatures = features.collect { it.toUpperCase() }
		Set upperCaseRequestedFeatures = featuresCriteria.requestedFeatures.collect { it.toUpperCase() }
		upperCaseRequestedFeatures.every { it in upperCaseAvailableFeatures }
	}
	
	double distanceInKmTo(Coordinates coordinates) {
		location.distanceInKmTo(coordinates)
	}
}
