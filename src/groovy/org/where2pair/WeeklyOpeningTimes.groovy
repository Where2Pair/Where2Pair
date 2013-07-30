package org.where2pair

import groovy.transform.Immutable

class WeeklyOpeningTimes {

	Map weeklyOpeningTimes
	
	boolean isOpen(OpenTimesCriteria openTimesCriteria) {
		weeklyOpeningTimes[openTimesCriteria.dayOfWeek]
			.isOpen(openTimesCriteria.openFrom, openTimesCriteria.openUntil)
	}
	
	def getAt(key) {
		weeklyOpeningTimes[key]
	}
	
	@Override
	void each(Closure c) {
		weeklyOpeningTimes.each(c)
	}
	
}
