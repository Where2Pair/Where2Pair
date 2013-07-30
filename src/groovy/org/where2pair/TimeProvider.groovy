package org.where2pair

import org.joda.time.DateTime
import org.where2pair.DailyOpeningTimes.SimpleTime

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
