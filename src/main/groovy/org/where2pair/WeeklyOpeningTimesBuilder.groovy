package org.where2pair

import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY

import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

class WeeklyOpeningTimesBuilder {

	Map weeklyOpeningTimes = (MONDAY..SUNDAY).collectEntries { [it, new DailyOpeningTimes() ] }
	
	WeeklyOpeningTimesBuilder addOpenPeriod(DayOfWeek day, SimpleTime openTime, SimpleTime closeTime) {
		DailyOpeningTimes dailyOpeningTimes = weeklyOpeningTimes[day]
		dailyOpeningTimes.openPeriods << new OpenPeriod(openTime, closeTime)
		this
	}
	
	WeeklyOpeningTimes build() {
		new WeeklyOpeningTimes(weeklyOpeningTimes: weeklyOpeningTimes)
	}
}
