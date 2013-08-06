package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class VenueWithDistanceDto {
	VenueDto venue
	double distanceInKm
}
