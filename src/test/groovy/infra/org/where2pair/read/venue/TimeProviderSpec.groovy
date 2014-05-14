package org.where2pair.read.venue

import org.joda.time.DateTime
import spock.lang.Specification

import static spock.util.matcher.HamcrestMatchers.closeTo

class TimeProviderSpec extends Specification {

    def timeProvider = Spy(TimeProvider)

    def 'returns current DateTime'() {
        given:
        long actualTime = new DateTime().millis

        when:
        long currentTime = timeProvider.currentDateTime.millis

        then:
        currentTime closeTo(actualTime, 1000)
    }

    def 'returns current time as simple time'() {
        given:
        def currentTime = new DateTime()
        timeProvider.getCurrentDateTime() >> currentTime

        when:
        def timeNow = timeProvider.timeNow()

        then:
        timeNow.hour == currentTime.hourOfDay
        timeNow.minute == currentTime.minuteOfHour
    }

    def 'returns today as DayOfWeek'() {
        given:
        def currentTime = new DateTime()
        timeProvider.getCurrentDateTime() >> currentTime

        when:
        def dayOfWeek = timeProvider.today()

        then:
        dayOfWeek == DayOfWeek.getDayOfWeek(currentTime)
    }
}
