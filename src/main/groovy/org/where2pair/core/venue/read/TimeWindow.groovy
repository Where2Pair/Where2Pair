package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.SimpleTime


@Immutable
class TimeWindow {
    SimpleTime from
    SimpleTime until

    boolean intersects(TimeWindow timeWindow) {
        from >= timeWindow.from && from < timeWindow.until && until <= timeWindow.until
    }
}
