package org.where2pair

import java.util.Iterator;

import groovy.transform.Immutable

import org.joda.time.DateTime

import static org.where2pair.DayOfWeek.getDayOfWeek

class WeeklyOpeningTimes {

	Map weeklyOpeningTimes
	
	boolean isOpen(DateTime dateTime) {
		DayOfWeek dayOfWeek = getDayOfWeek(dateTime)
		int hourOfDay = dateTime.getHourOfDay()
		int minuteOfHour = dateTime.getMinuteOfHour()
		
		weeklyOpeningTimes[dayOfWeek].isOpen(hourOfDay, minuteOfHour)
	}
	
	def getAt(key) {
		weeklyOpeningTimes[key]
	}
	
	@Override
	void each(Closure c) {
		weeklyOpeningTimes.each(c)
	}
	
}
