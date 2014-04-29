package org.where2pair.core.venue.read

import static org.where2pair.core.venue.read.DayOfWeek.MONDAY
import static org.where2pair.core.venue.read.DayOfWeek.SUNDAY

import groovy.transform.Immutable

@Immutable
class OpenHoursToJsonMapper {
    Map<String, ?> asOpenHoursJson(WeeklyOpeningTimes weeklyOpeningTimes) {
        Map openHours = (MONDAY..SUNDAY).collectEntries { [dayToString(it), []] }

        weeklyOpeningTimes.each { DayOfWeek day, DailyOpeningTimes dailyOpeningTimes ->
            dailyOpeningTimes.openPeriods.each { DailyOpeningTimes.OpenPeriod openPeriod ->
                openHours[dayToString(day)] << [
                        openHour: openPeriod.openFrom.hour,
                        openMinute: openPeriod.openFrom.minute,
                        closeHour: openPeriod.openUntil.hour,
                        closeMinute: openPeriod.openUntil.minute]
            }
        }

        openHours
    }

    private String dayToString(DayOfWeek day) {
        day.toString().toLowerCase()
    }
}
