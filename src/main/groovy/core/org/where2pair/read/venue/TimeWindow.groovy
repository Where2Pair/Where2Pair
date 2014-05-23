package org.where2pair.read.venue

import groovy.transform.Immutable
import org.where2pair.common.venue.SimpleTime

@Immutable
class TimeWindow {
    SimpleTime from
    SimpleTime until

    boolean supersetOf(TimeWindow timeWindow) {
        from <= timeWindow.from && until >= timeWindow.until
    }

    boolean contains(SimpleTime time) {
        from <= time && until >= time
    }
}

