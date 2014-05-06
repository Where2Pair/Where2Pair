package org.where2pair.core.venue.read.mappingtojson

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DailyOpeningTimes
import org.where2pair.core.venue.read.DailyOpeningTimes.OpenPeriod
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.TimeWindow
import org.where2pair.core.venue.read.WeeklyOpeningTimes

import static DayOfWeek.MONDAY
import static DayOfWeek.SUNDAY

class WeeklyOpeningTimesBuilder {

    Map<DayOfWeek, List<OpenPeriod>> dailyOpenPeriods = [:].withDefault { [] }

    WeeklyOpeningTimesBuilder addOpenPeriod(DayOfWeek day, SimpleTime openTime, SimpleTime closeTime) {
        TimeWindow openTimeWindow = new TimeWindow(openTime, closeTime)
        dailyOpenPeriods[day] << new OpenPeriod(openTimeWindow: openTimeWindow)
        this
    }

    WeeklyOpeningTimes build() {
        Map<DayOfWeek, DailyOpeningTimes> weeklyOpeningTimes =
        (MONDAY..SUNDAY).collectEntries { day ->
            [day, new DailyOpeningTimes(openPeriods: dailyOpenPeriods[day])]
        }
        new WeeklyOpeningTimes(weeklyOpeningTimes: weeklyOpeningTimes)
    }
}
