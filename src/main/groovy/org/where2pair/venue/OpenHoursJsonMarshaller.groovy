package org.where2pair.venue

import org.where2pair.venue.DailyOpeningTimes.OpenPeriod
import org.where2pair.venue.DailyOpeningTimes.SimpleTime

import static org.where2pair.venue.DayOfWeek.MONDAY
import static org.where2pair.venue.DayOfWeek.SUNDAY

class OpenHoursJsonMarshaller {

    Map asOpenHoursJson(WeeklyOpeningTimes weeklyOpeningTimes) {
        Map openHours = (MONDAY..SUNDAY).collectEntries { [dayToString(it), []] }

        weeklyOpeningTimes.each { DayOfWeek day, DailyOpeningTimes dailyOpeningTimes ->
            dailyOpeningTimes.openPeriods.each { OpenPeriod openPeriod ->
                openHours[dayToString(day)] << [
                        openHour: openPeriod.start.hour,
                        openMinute: openPeriod.start.minute,
                        closeHour: openPeriod.end.hour,
                        closeMinute: openPeriod.end.minute]
            }
        }

        openHours
    }

    private String dayToString(DayOfWeek day) {
        day.toString().toLowerCase()
    }

    WeeklyOpeningTimes asWeeklyOpeningTimes(Map openHours) {
        WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
        openHours.each { day, dailyOpenHours ->
            dailyOpenHours.each {
                builder.addOpenPeriod(DayOfWeek.parseDayOfWeek(day),
                        new SimpleTime(asInt(it.openHour), asInt(it.openMinute)),
                        new SimpleTime(asInt(it.closeHour), asInt(it.closeMinute)))
            }
        }
        builder.build()
    }

    private int asInt(timeUnit) {
        (timeUnit instanceof Integer) ? timeUnit : Integer.parseInt(timeUnit)
    }
}
