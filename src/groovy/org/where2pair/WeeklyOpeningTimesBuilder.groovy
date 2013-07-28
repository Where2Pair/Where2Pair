package org.where2pair

import static org.where2pair.DayOfWeek.FRIDAY
import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SATURDAY
import static org.where2pair.DayOfWeek.SUNDAY
import static org.where2pair.DayOfWeek.THURSDAY
import static org.where2pair.DayOfWeek.TUESDAY
import static org.where2pair.DayOfWeek.WEDNESDAY

import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

class WeeklyOpeningTimesBuilder {

	Map weeklyOpeningTimes = [
		(MONDAY): new DailyOpeningTimes(),
		(TUESDAY): new DailyOpeningTimes(),
		(WEDNESDAY): new DailyOpeningTimes(),
		(THURSDAY): new DailyOpeningTimes(),
		(FRIDAY): new DailyOpeningTimes(),
		(SATURDAY): new DailyOpeningTimes(),
		(SUNDAY): new DailyOpeningTimes(),
	]
	
	void addOpenPeriod(DayOfWeek day, SimpleTime openTime, SimpleTime closeTime) {
		DailyOpeningTimes dailyOpeningTimes = weeklyOpeningTimes[day]
		dailyOpeningTimes.openPeriods << new OpenPeriod(openTime, closeTime)
	}
	
	WeeklyOpeningTimes build() {
		new WeeklyOpeningTimes(weeklyOpeningTimes: weeklyOpeningTimes)
	}
}
