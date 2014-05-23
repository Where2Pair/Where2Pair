package org.where2pair.common.venue

import groovy.transform.Immutable

@Immutable
class SimpleTime implements Comparable {
    int hour
    int minute

    @SuppressWarnings('ExplicitCallToEqualsMethod')
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

