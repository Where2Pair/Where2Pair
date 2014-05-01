package org.where2pair.core.venue.read

import groovy.transform.Immutable
import org.where2pair.core.venue.common.SimpleTime


@Immutable
class TimeWindow {
    SimpleTime from
    SimpleTime until

    boolean supersetOf(TimeWindow timeWindow) {
        from <= timeWindow.from && until >= timeWindow.until
    }
}
