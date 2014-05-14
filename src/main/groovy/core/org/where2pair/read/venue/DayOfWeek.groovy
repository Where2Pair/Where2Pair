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

    public static DayOfWeek getDayOfWeek(DateTime dateTime) {
        (MONDAY..SUNDAY)[dateTime.getDayOfWeek() - 1]
    }

    public static DayOfWeek parseDayOfWeek(String day) {
        day.toUpperCase()
    }
}
