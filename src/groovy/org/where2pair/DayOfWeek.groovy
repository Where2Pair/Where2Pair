package org.where2pair

import org.joda.time.DateTime

enum DayOfWeek {

	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY,
	SUNDAY
	
	public static DayOfWeek getDayOfWeek(DateTime dateTime) {
		(MONDAY..SUNDAY)[dateTime.getDayOfWeek() - 1]
	}
	
	public static DayOfWeek parseString(String day) {
		day.toUpperCase()
	}
}
