package org.where2pair.core.venue

import org.joda.time.DateTime
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek
import org.where2pair.core.venue.read.TimeProvider
import spock.lang.Specification

import static spock.util.matcher.HamcrestMatchers.closeTo

class TimeProviderSpec extends Specification {

    TimeProvider timeProvider = Spy()

    def "should return current DateTime"() {
        given:
        long actualTime = new DateTime().millis

        when:
        long currentTime = timeProvider.currentDateTime.millis

        then:
        currentTime closeTo(actualTime, 1000)
    }

    def "should return current time as simple time"() {
        given:
        DateTime currentTime = new DateTime()
        timeProvider.getCurrentDateTime() >> currentTime

        when:
        SimpleTime timeNow = timeProvider.timeNow()

        then:
        timeNow.hour == currentTime.hourOfDay
        timeNow.minute == currentTime.minuteOfHour
    }

    def "should return today as DayOfWeek"() {
        given:
        DateTime currentTime = new DateTime()
        timeProvider.getCurrentDateTime() >> currentTime

        when:
        DayOfWeek dayOfWeek = timeProvider.today()

        then:
        dayOfWeek == DayOfWeek.getDayOfWeek(currentTime)
    }
}
