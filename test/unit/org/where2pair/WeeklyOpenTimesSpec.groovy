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
		Map weeklyOpeningTimes = weeklyOpeningTimes()
		WeeklyOpenTimes weeklyOpenTimes = new WeeklyOpenTimes(weeklyOpeningTimes: weeklyOpeningTimes)
		DateTime dateTime = DateTimeFormat.forPattern('yyyy-MM-dd').parseDateTime(date)
		DailyOpeningTimes daysOpeningTimes = weeklyOpeningTimes[dayOfWeek]
		
		when:
		weeklyOpenTimes.isOpen(dateTime)
		
		then:
		1 * daysOpeningTimes.isOpen(dateTime.getHourOfDay(), dateTime.getMinuteOfHour())
		
		where:
		date 			| dayOfWeek
		'2013-07-22'	| MONDAY
		'2013-07-23'	| TUESDAY
		'2013-07-24'	| WEDNESDAY
		'2013-07-25'	| THURSDAY
		'2013-07-26'	| FRIDAY
		'2013-07-27'	| SATURDAY
		'2013-07-28'	| SUNDAY
	}
	
	private Map weeklyOpeningTimes() {
		[
			(MONDAY): Mock(DailyOpeningTimes),
			(TUESDAY): Mock(DailyOpeningTimes),
			(WEDNESDAY): Mock(DailyOpeningTimes),
			(THURSDAY): Mock(DailyOpeningTimes),
			(FRIDAY): Mock(DailyOpeningTimes),
			(SATURDAY): Mock(DailyOpeningTimes),
			(SUNDAY): Mock(DailyOpeningTimes)
		]
	}
}
