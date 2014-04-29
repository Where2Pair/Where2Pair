package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.DayOfWeek


class VenueJsonValidator {

    void validate(Map<String, ?> venueJson) {
        ALL_VALIDATION_CHECKS.each { rule, reason ->
            if (!rule(venueJson)) throw new InvalidVenueJsonException(reason)
        }
    }

    private static final Map REQUIRED_FIELD_CHECKS = [
            { it.name }: 'Venue \'name\' must be provided',
            { it.location }: 'Venue \'location\' must be provided',
            { it.address }: 'Venue \'address\' must be provided',
            { it.openHours }: 'Venue \'openHours\' must be provided'
    ]

    private static final Map EXPECTED_STRUCTURE_CHECKS = [
            {
                it.address instanceof Map
            }: 'Expected address to be a map e.g. [addressLine1: \'9 Appold Street\', city: \'London\'...]',
            { it.location instanceof Map }: 'Expected location to be a map e.g. [latitude: 1.0, longitude: 0.1]',
            {
                it.openHours instanceof Map
            }: 'Expected openHours to map day to a list of open periods e.g. [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],\ntuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],\n...\n]',
            {
                it.openHours.every { day, dailyOpeningTimes ->
                    dailyOpeningTimes instanceof List && dailyOpeningTimes.every { it instanceof Map }
                }
            }: 'Expected openHours to map day to a list of open periods e.g. [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],\ntuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],\n...\n]'
    ]

    private static final Map LOCATION_CHECKS = [
            { it.location.latitude }: 'Venue \'location.latitude\' must be provided',
            { it.location.longitude }: 'Venue \'location.longitude\' must be provided'
    ]

    private static final Map ADDRESS_CHECKS = [
            { it.address.addressLine1 }: 'Venue \'address.addressLine1\' must be provided',
            { it.address.city }: 'Venue \'address.city\' must be provided',
            { it.address.postcode }: 'Venue \'address.postcode\' must be provided'
    ]

    private static final Map OPEN_HOURS_CHECKS = [
            {
                it.openHours.any { day, dailyOpenHours ->
                    day in daysOfWeek()
                }
            }: 'Venue \'openHours\' must contain at least one open day (monday-sunday)',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { Map openPeriod ->
                                openPeriod.openHour != null &&
                                        openPeriod.openMinute != null &&
                                        openPeriod.closeHour != null &&
                                        openPeriod.closeMinute != null
                            }
                }
            }: 'Daily venue \'openHours\' must contain \'openHour\', \'openMinute\', \'closeHour\' and \'closeHour\'',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.openHour >= 0 }
                }
            }: '\'openHour\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.openMinute >= 0 }
                }
            }: '\'openMinute\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.openHour < 24 }
                }
            }: '\'openHour\' must be less than 24',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.openMinute < 60 }
                }
            }: '\'openMinute\' must be less than 60',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.closeHour >= 0 }
                }
            }: '\'closeHour\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.closeMinute >= 0 }
                }
            }: '\'closeMinute\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.closeHour < 24 }
                }
            }: '\'closeHour\' must be less than 24',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { it.closeMinute < 60 }
                }
            }: '\'closeMinute\' must be less than 60',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { Map openPeriod ->
                                SimpleTime openTime = new SimpleTime(openPeriod.openHour, openPeriod.openMinute)
                                SimpleTime closeTime = new SimpleTime(openPeriod.closeHour, openPeriod.closeMinute)
                                openTime < closeTime
                            }
                }
            }: 'Daily venue \'openHours\' must close after opening time'
    ]

    private static Set<String> daysOfWeek() {
        DayOfWeek.values().collect { it.toString().toLowerCase() }
    }

    private static Map<String, ?> allValidOpenHours(Map<String, ?> openHours) {
        openHours.subMap(daysOfWeek().intersect(openHours.keySet()))
    }

    private static final Map ALL_VALIDATION_CHECKS =
            REQUIRED_FIELD_CHECKS +
                    EXPECTED_STRUCTURE_CHECKS +
                    LOCATION_CHECKS +
                    ADDRESS_CHECKS +
                    OPEN_HOURS_CHECKS
}
