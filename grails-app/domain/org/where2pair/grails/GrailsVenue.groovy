package org.where2pair.grails

class GrailsVenue {

	double latitude
	double longitude
	static hasMany = [openPeriods: GrailsOpenPeriod]
	
    static constraints = {
		latitude min: -120.9762d, max: 41.25d
		longitude min: -31.96d, max: 115.84d
    }
	
	static mapping = {
		table 'venue'
		openPeriods lazy: false
	}
}
