package org.where2pair

import groovy.transform.EqualsAndHashCode;
import groovy.transform.Immutable

@EqualsAndHashCode
class DailyOpeningTimes {

	List openPeriods = []
	
	boolean isOpen(int hour, int minute) {
		if (openPeriods.isEmpty())
			return false
			
		OpenPeriod openPeriod = openPeriods[0]
		openPeriod.isOpen(new SimpleTime(hour, minute))
	}
	
	@Immutable
	static class OpenPeriod {
		SimpleTime start
		SimpleTime end
		
		boolean isOpen(SimpleTime time) {
			time >= start && time <= end
		}
	}
	
	@Immutable
	static class SimpleTime implements Comparable {
		int hour
		int minute
		
		int compareTo(otherTime) {
			if (this.equals(otherTime))
				return 0
			if (hour > otherTime.hour)
				return 1
			if (hour == otherTime.hour)
				return minute > otherTime.minute ? 1 : -1
			-1
		}
	}
	
}
