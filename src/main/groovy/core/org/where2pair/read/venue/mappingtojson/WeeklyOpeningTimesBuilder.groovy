package org.where2pair.read.venue.mappingtojson

import static org.where2pair.read.venue.DayOfWeek.MONDAY
import static org.where2pair.read.venue.DayOfWeek.SUNDAY

import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DailyOpeningTimes
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.TimeWindow
import org.where2pair.read.venue.WeeklyOpeningTimes

class WeeklyOpeningTimesBuilder {

    Map<DayOfWeek, List<DailyOpeningTimes.OpenPeriod>> dailyOpenPeriods = [:].withDefault { [] }

    WeeklyOpeningTimesBuilder addOpenPeriod(DayOfWeek day, SimpleTime openTime, SimpleTime closeTime) {
        TimeWindow openTimeWindow = new TimeWindow(openTime, closeTime)
        dailyOpenPeriods[day] << new DailyOpeningTimes.OpenPeriod(openTimeWindow: openTimeWindow)
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

