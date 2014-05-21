package org.where2pair.read.venue.find

import groovy.transform.TupleConstructor
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.read.venue.TimeProvider

@TupleConstructor
class OpenTimesCriteriaFactory {

    final TimeProvider timeProvider

    OpenTimesCriteria createOpenTimesCriteria(SimpleTime openFrom, SimpleTime openUntil, DayOfWeek openDay) {
        if (!openFrom && !openUntil && !openDay)
            return new OpenAnyTimeCriteria()

        if (!openFrom && !openUntil && openDay)
            return new OpenAnyTimeOnDayCriteria(openDay)

        if (openFrom && openUntil && openDay)
            return new OpenBetweenTimesOnDayCriteria(openFrom, openUntil, openDay)

        if ((openFrom || openUntil) && openDay)
            return new OpenAtTimeOnDayCriteria(openFrom ?: openUntil, openDay)

        if (openFrom && openUntil)
            return new OpenBetweenTimesOnDayCriteria(openFrom, openUntil, timeProvider.today())

        return new OpenAtTimeOnDayCriteria(openFrom ?: openUntil, timeProvider.today())
    }


}
