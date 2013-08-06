package org.where2pair

import groovy.transform.EqualsAndHashCode;
import groovy.transform.Immutable

@EqualsAndHashCode
class DailyOpeningTimes {
	List openPeriods = []
	
	boolean isOpen(SimpleTime openFrom, SimpleTime openUntil) {
		openPeriods.find { it.isOpen(openFrom, openUntil) }
	}
	
	@Immutable
	static class OpenPeriod {
		SimpleTime start
		SimpleTime end
		
		boolean isOpen(SimpleTime openFrom, SimpleTime openUntil) {
			openFrom >= start && openFrom < end && openUntil <= end
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
