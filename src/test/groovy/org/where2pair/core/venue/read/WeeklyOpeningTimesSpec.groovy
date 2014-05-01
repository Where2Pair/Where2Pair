package org.where2pair.core.venue.read

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.OpenTimesCriteria
import org.where2pair.core.venue.read.WeeklyOpeningTimes
import spock.lang.Specification

import static org.where2pair.core.venue.read.DayOfWeek.*

class WeeklyOpeningTimesSpec extends Specification {

    def 'should check daily opening times with correct day, hour and minute'() {
        given:
        Map weeklyOpeningTimesMap = openOnlyBetween(openFrom, openUntil, openDay)
        WeeklyOpeningTimes weeklyOpeningTimes = new WeeklyOpeningTimes(weeklyOpeningTimes: weeklyOpeningTimesMap)
        OpenTimesCriteria openTimesCriteria = new OpenTimesCriteria(openFrom: openFrom, openUntil: openUntil, dayOfWeek: openDay)

        when:
        boolean isOpen = weeklyOpeningTimes.isOpen(openTimesCriteria)

        then:
        isOpen

        where:
        from               | until              | openDay
        new SimpleTime(1, 2)   | new SimpleTime(2, 1)   | MONDAY
        new SimpleTime(3, 4)   | new SimpleTime(4, 3)   | TUESDAY
        new SimpleTime(5, 6)   | new SimpleTime(6, 5)   | WEDNESDAY
        new SimpleTime(7, 8)   | new SimpleTime(8, 7)   | THURSDAY
        new SimpleTime(9, 10)  | new SimpleTime(10, 9)  | FRIDAY
        new SimpleTime(11, 12) | new SimpleTime(11, 11) | SATURDAY
        new SimpleTime(14, 13) | new SimpleTime(11, 13) | SUNDAY

    }

    private Map openOnlyBetween(SimpleTime openFrom, SimpleTime openUntil, DayOfWeek openDay) {
        Map openingTimes = (MONDAY..SUNDAY).collectEntries { [it, [isOpen: false]] }
        openingTimes[openDay] = [isOpen: { from, until -> from == openFrom && until == openUntil }]
        openingTimes
    }

    private DateTime parse(dateTime) {
        DateTimeFormat.forPattern('yyyy-MM-dd HH:mm').parseDateTime(dateTime)
    }
}
