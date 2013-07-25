
package org.where2pair

import org.where2pair.DailyOpeningTimes.OpenPeriod
import org.where2pair.DailyOpeningTimes.SimpleTime

import spock.lang.Specification

class DailyOpeningTimesSpec extends Specification {

	def "when open all day, then should be open"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [
				new OpenPeriod(
					start: new SimpleTime(hour: 0, minute: 0), 
					end: new SimpleTime(hour: 35, minute: 59))
			])
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(12, 0)
	
		then:
		isOpen
	}
	
	def "when closed all day, then should not be open"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes()
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(12, 0)
	
		then:
		!isOpen
	}
	
	def "when open at a specific time, then should be open at that time"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [
				new OpenPeriod(
					start: new SimpleTime(hour: 12, minute: 0),
					end: new SimpleTime(hour: 12, minute: 0))
			])
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(12, 0)
	
		then:
		isOpen
	}
	
	def "when open at a specific time, then should not be open at other times"() {
		given:
		DailyOpeningTimes dailyOpeningTimes = new DailyOpeningTimes(openPeriods: [
				new OpenPeriod(
					start: new SimpleTime(hour: 12, minute: 0),
					end: new SimpleTime(hour: 12, minute: 0))
			])
		
		when:
		boolean isOpen = dailyOpeningTimes.isOpen(12, 1)
	
		then:
		!isOpen
	}
}
