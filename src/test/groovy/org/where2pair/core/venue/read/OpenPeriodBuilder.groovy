package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.SimpleTime


class OpenPeriodBuilder {
    DayOfWeek dayOfWeek
    SimpleTime openTime
    SimpleTime closeTime

    static OpenPeriodBuilder on(DayOfWeek dayOfWeek) {
        new OpenPeriodBuilder(dayOfWeek)
    }

    private OpenPeriodBuilder(DayOfWeek dayOfWeek) {
        this.dayOfWeek = dayOfWeek
    }

    OpenPeriodBuilder from(int hour) {
        this.openTime = new SimpleTime(hour, 0)
        this
    }

    OpenPeriodBuilder until(int hour) {
        this.closeTime = new SimpleTime(hour, 0)
        this
    }
}
