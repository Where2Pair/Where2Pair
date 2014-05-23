package org.where2pair.read.venue.mappingtojson

import static org.where2pair.read.venue.DayOfWeek.MONDAY
import static org.where2pair.read.venue.DayOfWeek.SUNDAY

import groovy.transform.Immutable
import org.where2pair.read.venue.DailyOpeningTimes
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.WeeklyOpeningTimes

@Immutable
class OpenHoursToJsonMapper {

    Map<String, ?> toJsonStructure(WeeklyOpeningTimes weeklyOpeningTimes) {
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

