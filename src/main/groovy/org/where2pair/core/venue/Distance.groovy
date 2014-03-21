package org.where2pair.core.venue

import groovy.transform.EqualsAndHashCode;

@EqualsAndHashCode
class Distance implements Comparable {
	double value
	DistanceUnit unit

	@Override
	int compareTo(Object o) {
		value <=> o.value
	}	
	
}
