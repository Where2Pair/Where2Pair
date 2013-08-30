package org.where2pair.venue.find

import org.joda.time.DateTime
import org.where2pair.venue.DailyOpeningTimes.SimpleTime
import org.where2pair.venue.DayOfWeek;

class TimeProvider {

	DateTime getCurrentDateTime() {
		new DateTime()
	}
	
	SimpleTime timeNow() {
		DateTime currentTime = getCurrentDateTime()
		new SimpleTime(currentTime.hourOfDay, currentTime.minuteOfHour)
	}
	
	DayOfWeek today() {
		DayOfWeek.getDayOfWeek(getCurrentDateTime())
	}
}
