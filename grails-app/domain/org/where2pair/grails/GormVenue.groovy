package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class GormVenue {

	String name
	double latitude
	double longitude
	String addressLine1
	String addressLine2
	String addressLine3
	String city
	String postcode
	String phoneNumber
	
	static hasMany = [openPeriods: GormOpenPeriod, features: String]
	
    static constraints = {
		name blank: false
		latitude min: -90d, max: 90d
		longitude min: -180d, max: 180d
		addressLine1 nullable: true
		addressLine2 nullable: true
		addressLine3 nullable: true
		city nullable: true
		postcode nullable: true
		phoneNumber nullable: true
		openPeriods minSize: 1
    }
	
	static mapping = {
		table 'venue'
		openPeriods lazy: false
		features lazy: false
	}
}
