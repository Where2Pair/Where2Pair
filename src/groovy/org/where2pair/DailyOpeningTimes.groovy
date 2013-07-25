package org.where2pair

import org.joda.time.DateTime

class DailyOpeningTimes {

	List openPeriods
	
	boolean isOpen(int hours, int minutes) {
		openPeriods
	}
	
	static class OpenPeriod {
		SimpleTime start
		SimpleTime end
	}
	
	static class SimpleTime {
		int hour
		int minute
	}
	
}
