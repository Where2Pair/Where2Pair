package org.where2pair

import static org.joda.time.DateTimeConstants.FRIDAY
import static org.joda.time.DateTimeConstants.MONDAY
import static org.joda.time.DateTimeConstants.SATURDAY
import static org.joda.time.DateTimeConstants.SUNDAY
import static org.joda.time.DateTimeConstants.THURSDAY
import static org.joda.time.DateTimeConstants.TUESDAY
import static org.joda.time.DateTimeConstants.WEDNESDAY

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
	
	void addOpenPeriod(int day, SimpleTime openTime, SimpleTime closeTime) {
		DailyOpeningTimes dailyOpeningTimes = weeklyOpeningTimes[day]
		dailyOpeningTimes.openPeriods << new OpenPeriod(openTime, closeTime)
	}
	
	WeeklyOpeningTimes build() {
		new WeeklyOpeningTimes(weeklyOpeningTimes: weeklyOpeningTimes)
	}
}
