package org.where2pair.grails

import org.where2pair.DayOfWeek;

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;

@EqualsAndHashCode
@ToString
class GormOpenPeriod {

	DayOfWeek day
	int openHour
	int openMinute
	int closeHour
	int closeMinute
	
	static belongsTo = GormVenue
		
    static constraints = {
		day enumType:"ordinal"
		openHour range: 0..35
		openMinute range: 0..59
		closeHour range: 0..35
		closeMinute range: 0..59
    }
	
	static mapping = {
		table 'open_period'
	}
}
