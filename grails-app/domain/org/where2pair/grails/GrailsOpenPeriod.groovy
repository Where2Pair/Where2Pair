package org.where2pair.grails

class GrailsOpenPeriod {

	int day
	int openHour
	int openMinute
	int closeHour
	int closeMinute
	static belongsTo = [venue: GrailsVenue]
		
    static constraints = {
		day range: 1..7
		openHour range: 0..35
		openMinute range: 0..59
		closeHour range: 0..35
		closeMinute range: 0..59
    }
	
	static mapping = {
		table 'open_period'
	}
}
