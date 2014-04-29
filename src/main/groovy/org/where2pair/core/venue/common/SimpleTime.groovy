package org.where2pair.core.venue.common

import groovy.transform.Immutable

@Immutable
class SimpleTime implements Comparable {
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
