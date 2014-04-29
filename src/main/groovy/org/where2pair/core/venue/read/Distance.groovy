package org.where2pair.core.venue.read

import groovy.transform.Immutable;

@Immutable
class Distance implements Comparable {
    double value
    DistanceUnit unit

    @Override
    int compareTo(Object o) {
        value<=>o.value
    }

}
