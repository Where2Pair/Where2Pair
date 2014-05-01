package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DailyOpeningTimes
import org.where2pair.core.venue.read.DailyOpeningTimes.OpenPeriod
import org.where2pair.core.venue.common.SimpleTime
import spock.lang.Specification

class DailyOpeningTimesSpec extends Specification {

    def "when closed all day, should not be open"() {
        given:
        DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [])

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(new SimpleTime(0, 0), new SimpleTime(35, 59))

        then:
        !isOpen
    }

    def "given a single open period, should determine when open"() {
        given:
        DailyOpeningTimes dailyOpeningTimes = openingTimesWith(startTime, endTime)

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(openFrom, openUntil)

        then:
        isOpen == expectedOpen

        where:
        startTime | endTime | from               | until              | expectedOpen
        '12:00'   | '12:00' | new SimpleTime(12, 0)  | new SimpleTime(12, 0)  | false
        '12:00'   | '12:01' | new SimpleTime(12, 0)  | new SimpleTime(12, 1)  | true
        '12:00'   | '12:00' | new SimpleTime(12, 1)  | new SimpleTime(12, 1)  | false
        '12:00'   | '12:00' | new SimpleTime(11, 59) | new SimpleTime(11, 59) | false
        '00:00'   | '35:59' | new SimpleTime(12, 0)  | new SimpleTime(18, 0)  | true
        '12:00'   | '12:59' | new SimpleTime(12, 0)  | new SimpleTime(12, 59) | true
        '12:00'   | '12:30' | new SimpleTime(12, 15) | new SimpleTime(12, 31) | false
    }

    def "given multiple open times, should check each to see if there is an open time"() {
        given:
        DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [
                new OpenPeriod(
                        start: new SimpleTime(1, 0),
                        end: new SimpleTime(2, 0)),
                new OpenPeriod(
                        start: new SimpleTime(12, 0),
                        end: new SimpleTime(13, 0))
        ])

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(new SimpleTime(12, 0), new SimpleTime(13, 0))

        then:
        isOpen
    }

    private DailyOpeningTimes openingTimesWith(startTime, endTime) {
        def (startHour, startMinute) = parse(startTime)
        def (endHour, endMinute) = parse(endTime)
        new DailyOpeningTimes(openPeriods: [
                new OpenPeriod(
                        start: new SimpleTime(startHour, startMinute),
                        end: new SimpleTime(endHour, endMinute))
        ])
    }

    private List parse(time) {
        time.split(':').collect { Integer.parseInt(it) }
    }
}
