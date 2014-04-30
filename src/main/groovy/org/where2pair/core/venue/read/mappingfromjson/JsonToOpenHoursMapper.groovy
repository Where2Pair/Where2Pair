package org.where2pair.core.venue.read.mappingfromjson

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.WeeklyOpeningTimes
import org.where2pair.core.venue.read.mappingtojson.WeeklyOpeningTimesBuilder

class JsonToOpenHoursMapper {

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
