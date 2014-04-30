package org.where2pair.core.venue.read.mappingtojson

import org.where2pair.core.venue.read.DailyOpeningTimes
import org.where2pair.core.venue.read.DailyOpeningTimes.OpenPeriod
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.TimeWindow
import org.where2pair.core.venue.read.WeeklyOpeningTimes

import static DayOfWeek.SUNDAY

class WeeklyOpeningTimesBuilder {

    Map weeklyOpeningTimes = (DayOfWeek.MONDAY..SUNDAY).collectEntries { [it, new DailyOpeningTimes()] }

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
