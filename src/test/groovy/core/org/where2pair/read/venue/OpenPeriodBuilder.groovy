package org.where2pair.read.venue

import org.where2pair.common.venue.SimpleTime


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

    OpenPeriodBuilder from(SimpleTime openTime) {
        this.openTime = openTime
        this
    }

    OpenPeriodBuilder until(SimpleTime closeTime) {
        this.closeTime = closeTime
        this
    }
}
