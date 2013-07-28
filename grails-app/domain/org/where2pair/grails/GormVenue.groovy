package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class GormVenue {

	double latitude
	double longitude
	Set openPeriods
	
	static hasMany = [openPeriods: GormOpenPeriod]
	
    static constraints = {
		latitude min: -120.9762d, max: 41.25d
		longitude min: -31.96d, max: 115.84d
    }
	
	static mapping = {
		table 'venue'
		openPeriods lazy: false
	}
}
