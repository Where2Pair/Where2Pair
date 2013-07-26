package org.where2pair

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.joda.time.format.DateTimeFormatter
import org.joda.time.format.DateTimeParser

import spock.lang.Specification
import static org.joda.time.DateTimeConstants.MONDAY
import static org.joda.time.DateTimeConstants.TUESDAY
import static org.joda.time.DateTimeConstants.WEDNESDAY
import static org.joda.time.DateTimeConstants.THURSDAY
import static org.joda.time.DateTimeConstants.FRIDAY
import static org.joda.time.DateTimeConstants.SATURDAY
import static org.joda.time.DateTimeConstants.SUNDAY

class WeeklyOpenTimesSpec extends Specification {

	def "should check daily opening times on correct day"() {
		given:
		Map weeklyOpeningTimes = openOnlyOn(openDay, openTimestamp)
		WeeklyOpenTimes weeklyOpenTimes = new WeeklyOpenTimes(weeklyOpeningTimes)
		DateTime dateTime = parse(openTimestamp)
		
		when:
		boolean isOpen = weeklyOpenTimes.isOpen(dateTime)
		
		then:
		isOpen
		
		where:
		openTimestamp		| openDay
		'2013-07-22 19:22'	| MONDAY
		'2013-07-23 14:21'	| TUESDAY
		'2013-07-24 00:00'	| WEDNESDAY
		'2013-07-25 18:55'	| THURSDAY
		'2013-07-26 09:40'	| FRIDAY
		'2013-07-27 18:50'	| SATURDAY
		'2013-07-28 22:39'	| SUNDAY
		
	}
	
	private Map openOnlyOn(int openDay, String openTimestamp) {
		DateTime openDateTime = parse(openTimestamp)
		Map openingTimes = (MONDAY..SUNDAY).collectEntries { [it, [isOpen: false ]] }
		openingTimes[openDay] = [isOpen: { hour, minute -> hour == openDateTime.getHourOfDay() && minute == openDateTime.getMinuteOfHour() }]
		openingTimes
	}
	
	private DateTime parse(dateTime) {
		DateTimeFormat.forPattern('yyyy-MM-dd HH:mm').parseDateTime(dateTime)
	}
}
