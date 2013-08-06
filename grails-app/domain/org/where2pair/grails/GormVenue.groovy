package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class GormVenue {

	String name
	double latitude
	double longitude
	Set openPeriods
	String addressLine1
	String addressLine2
	String addressLine3
	String city
	String postcode
	String phoneNumber
	List features = []
	
	static hasMany = [openPeriods: GormOpenPeriod]
	
    static constraints = {
		name nullable: false, blank: false
		addressLine1 nullable: true
		addressLine2 nullable: true
		addressLine3 nullable: true
		city nullable: true
		postcode nullable: true
		phoneNumber nullable: true
		latitude min: -90d, max: 90d
		longitude min: -180d, max: 180d
    }
	
	static mapping = {
		table 'venue'
		openPeriods lazy: false
	}
}
