package org.where2pair.core.venue.read

import org.where2pair.core.venue.read.DailyOpeningTimes.OpenPeriod
import org.where2pair.core.venue.common.SimpleTime

import static DayOfWeek.MONDAY
import static DayOfWeek.SUNDAY

class WeeklyOpeningTimesBuilder {

    Map weeklyOpeningTimes = (MONDAY..SUNDAY).collectEntries { [it, new DailyOpeningTimes()] }

    WeeklyOpeningTimesBuilder addOpenPeriod(DayOfWeek day, SimpleTime openTime, SimpleTime closeTime) {
        DailyOpeningTimes dailyOpeningTimes = weeklyOpeningTimes[day]
        TimeWindow timeWindow = new TimeWindow(openTime, closeTime)
        dailyOpeningTimes.openPeriods << new OpenPeriod(timeWindow)
        this
    }

    WeeklyOpeningTimes build() {
        new WeeklyOpeningTimes(weeklyOpeningTimes)
    }
}
