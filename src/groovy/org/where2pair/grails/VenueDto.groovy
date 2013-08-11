package org.where2pair.grails

import groovy.transform.EqualsAndHashCode
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class VenueDto {
    long id
    String name
	double latitude
	double longitude
	String addressLine1
	String addressLine2
	String addressLine3
	String city
	String postcode
	String phoneNumber
	Map openHours
	Set features
}
