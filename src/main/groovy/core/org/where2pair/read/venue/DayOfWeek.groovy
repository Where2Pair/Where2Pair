package org.where2pair.read.venue

import org.joda.time.DateTime

enum DayOfWeek {

    MONDAY,
    TUESDAY,
    WEDNESDAY,
    THURSDAY,
    FRIDAY,
    SATURDAY,
    SUNDAY

    static DayOfWeek getDayOfWeek(DateTime dateTime) {
        (MONDAY..SUNDAY)[dateTime.dayOfWeek - 1]
    }

    static DayOfWeek parseDayOfWeek(String day) {
        day.toUpperCase()
    }
}

