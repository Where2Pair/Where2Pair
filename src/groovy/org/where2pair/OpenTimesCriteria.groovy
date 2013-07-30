package org.where2pair

import groovy.transform.EqualsAndHashCode;
import groovy.transform.Immutable;
import groovy.transform.ToString;

import org.where2pair.DailyOpeningTimes.SimpleTime

@EqualsAndHashCode
@ToString
class OpenTimesCriteria {
	SimpleTime openFrom
	SimpleTime openUntil
	DayOfWeek dayOfWeek
}
