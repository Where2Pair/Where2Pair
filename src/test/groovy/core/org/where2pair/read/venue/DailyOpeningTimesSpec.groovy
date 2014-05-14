package org.where2pair.read.venue

import org.where2pair.common.venue.SimpleTime
import spock.lang.Specification
import spock.lang.Unroll

class DailyOpeningTimesSpec extends Specification {

    def 'when closed all day, should not be open'() {
        given:
        DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [])

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(new TimeWindow(new SimpleTime(0, 0), new SimpleTime(35, 59)))

        then:
        !isOpen
    }

    @Unroll
    def 'given a single open period, should determine when open'() {
        given:
        DailyOpeningTimes dailyOpeningTimes = openingTimesWith(startTime, endTime)

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(new TimeWindow(from, until))

        then:
        isOpen == expectedOpen

        where:
        startTime | endTime | from                   | until                  | expectedOpen
        '12:00'   | '12:00' | new SimpleTime(12, 0)  | new SimpleTime(12, 0)  | true
        '12:00'   | '12:01' | new SimpleTime(12, 0)  | new SimpleTime(12, 1)  | true
        '12:00'   | '12:00' | new SimpleTime(12, 1)  | new SimpleTime(12, 1)  | false
        '12:00'   | '12:00' | new SimpleTime(11, 59) | new SimpleTime(11, 59) | false
        '00:00'   | '35:59' | new SimpleTime(12, 0)  | new SimpleTime(18, 0)  | true
        '12:00'   | '12:59' | new SimpleTime(12, 0)  | new SimpleTime(12, 59) | true
        '12:00'   | '12:30' | new SimpleTime(12, 15) | new SimpleTime(12, 31) | false
    }

    def 'given multiple open times, should check each to see if there is an open time'() {
        given:
        DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [
                new DailyOpeningTimes.OpenPeriod(openTimeWindow: new TimeWindow(
                        new SimpleTime(1, 0),
                        new SimpleTime(2, 0))),
                new DailyOpeningTimes.OpenPeriod(openTimeWindow: new TimeWindow(
                        new SimpleTime(12, 0),
                        new SimpleTime(13, 0)))
        ])

        when:
        boolean isOpen = dailyOpeningTimes.isOpen(new TimeWindow(new SimpleTime(12, 0), new SimpleTime(13, 0)))

        then:
        isOpen
    }

    private static DailyOpeningTimes openingTimesWith(startTime, endTime) {
        def (int startHour, int startMinute) = parse(startTime)
        def (int endHour, int endMinute) = parse(endTime)
        new DailyOpeningTimes(openPeriods: [
                new DailyOpeningTimes.OpenPeriod(openTimeWindow: new TimeWindow(
                        new SimpleTime(startHour, startMinute),
                        new SimpleTime(endHour, endMinute)))
        ])
    }

    private static List parse(time) {
        time.split(':').collect { Integer.parseInt(it) }
    }
}
