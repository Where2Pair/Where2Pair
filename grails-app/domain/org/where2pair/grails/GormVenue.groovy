package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString

@EqualsAndHashCode
@ToString
class GormVenue {

	double latitude
	double longitude
    String name
	Set openPeriods
	
	static hasMany = [openPeriods: GormOpenPeriod]
	
    static constraints = {
		latitude min: -90d, max: 90d
		longitude min: -180d, max: 180d
    }
	
	static mapping = {
		table 'venue'
		openPeriods lazy: false
	}
}
