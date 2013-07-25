
package org.where2pair

import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class DailyOpeningTimesSpec extends Specification {

	def "when closed all day, should not be open"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [])
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(12, 0)
	
		then:
		!isOpen
	}
	
	def "given a single open period, should determine when open"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = openingTimesWith(startTime, endTime)
		def (currentHour, currentMinute) = parse(currentTime)
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(currentHour, currentMinute)
		
		then:
		isOpen == expectedOpen
		
		where:
		startTime 	| endTime | currentTime | expectedOpen
		'12:00'		| '12:00' | '12:00'		| true
		'12:00'		| '12:00' | '12:01'		| false
		'12:00'		| '12:00' | '11:59'		| false
		'00:00'		| '35:59' | '12:00'		| true
		'12:00'		| '12:59' | '12:30'		| true
		'12:00'		| '12:30' | '12:59'		| false
	}
	
	private DailyOpeningTimes openingTimesWith(startTime, endTime) {
		def (startHour, startMinute) = parse(startTime)
		def (endHour, endMinute) = parse(endTime)
		new DailyOpeningTimes(openPeriods: [
			new OpenPeriod(
				start: new SimpleTime(hour: startHour, minute: startMinute),
				end: new SimpleTime(hour: endHour, minute: endMinute))
		])
	}
	
	private List parse(time) {
		time.split(':').collect { Integer.parseInt(it) }
	}
}
