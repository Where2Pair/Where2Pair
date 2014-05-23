package org.where2pair.read.venue

import groovy.transform.Immutable

@Immutable
class Distance implements Comparable {
    double value
    DistanceUnit unit

    @Override
    int compareTo(Object o) {
        value <=> o.value
    }

}

