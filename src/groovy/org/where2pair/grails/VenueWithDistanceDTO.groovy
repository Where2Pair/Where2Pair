package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class VenueWithDistanceDTO {
	VenueDTO venue
	double distanceInKm
}
