package org.where2pair.grails

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class GormOpenPeriod {

	int day
	int openHour
	int openMinute
	int closeHour
	int closeMinute
	
	static belongsTo = GormVenue
		
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
