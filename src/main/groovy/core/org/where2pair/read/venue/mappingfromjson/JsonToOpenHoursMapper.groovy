package org.where2pair.read.venue.mappingfromjson

import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.WeeklyOpeningTimes
import org.where2pair.read.venue.mappingtojson.WeeklyOpeningTimesBuilder

import static org.where2pair.read.venue.DayOfWeek.parseDayOfWeek


class JsonToOpenHoursMapper {

    WeeklyOpeningTimes asWeeklyOpeningTimes(Map openHours) {
        WeeklyOpeningTimesBuilder builder = new WeeklyOpeningTimesBuilder()
        openHours.each { day, dailyOpenHours ->
            dailyOpenHours.each {
                builder.addOpenPeriod(parseDayOfWeek(day),
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
