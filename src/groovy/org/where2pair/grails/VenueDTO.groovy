package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class VenueDTO {
	double latitude
	double longitude
    String name
	Map openHours
}
