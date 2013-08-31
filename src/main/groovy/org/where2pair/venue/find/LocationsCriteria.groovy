package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class LocationsCriteria {
	List locations
	DistanceUnit distanceUnit
	
	int size() {
		locations.size()
	}
}
