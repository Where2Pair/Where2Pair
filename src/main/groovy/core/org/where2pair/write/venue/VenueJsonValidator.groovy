package org.where2pair.write.venue

import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.DayOfWeek
import org.where2pair.common.venue.Facility


class VenueJsonValidator {

    void validate(Map<String, ?> venueJson) throws InvalidVenueJsonException {
        ALL_VALIDATION_CHECKS.each { rule, reason ->
            if (!rule(venueJson)) {
                throw new InvalidVenueJsonException(reason)
            }
        }
    }

    private static final REQUIRED_FIELD_CHECKS = [
            { it.name }: 'Venue \'name\' must be provided',
            { it.location }: 'Venue \'location\' must be provided',
            { it.address }: 'Venue \'address\' must be provided',
            { it.openHours }: 'Venue \'openHours\' must be provided'
    ]

    private static final EXPECTED_STRUCTURE_CHECKS = [
            {
                it.address instanceof Map
            }: ADDRESS_STRUCTURE_ERROR_MESSAGE,
            {
                it.location instanceof Map
            }: LOCATION_STRUCTURE_ERROR_MESSAGE,
            {
                it.openHours instanceof Map
            }: OPEN_HOURS_STRUCTURE_ERROR_MESSAGE,
            {
                it.openHours.every { day, dailyOpeningTimes ->
                    dailyOpeningTimes instanceof List &&
                            dailyOpeningTimes.every { it instanceof Map }
                }
            }: OPEN_HOURS_STRUCTURE_ERROR_MESSAGE,
            {
                !it.facilities  || it.facilities instanceof Map
            }: FACILITIES_STRUCTURE_ERROR_MESSAGE
    ]

    private static final LOCATION_CHECKS = [
            { it.location.latitude }: 'Venue \'location.latitude\' must be provided',
            { it.location.longitude }: 'Venue \'location.longitude\' must be provided'
    ]

    private static final ADDRESS_CHECKS = [
            { it.address.addressLine1 }: 'Venue \'address.addressLine1\' must be provided',
            { it.address.city }: 'Venue \'address.city\' must be provided',
            { it.address.postcode }: 'Venue \'address.postcode\' must be provided'
    ]

    private static final OPEN_HOURS_CHECKS = [
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
                            dailyOpeningHours.every {
                                isInteger(it.openHour)
                            }
                }
            }: '\'openHour\' must be an integer',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every {
                                isInteger(it.openMinute)
                            }
                }
            }: '\'openMinute\' must be an integer',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every {
                                isInteger(it.closeHour)
                            }
                }
            }: '\'closeHour\' must be an integer',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every {
                                isInteger(it.closeMinute)
                            }
                }
            }: '\'closeMinute\' must be an integer',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.openHour as Integer) >= 0 }
                }
            }: '\'openHour\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.openMinute as Integer) >= 0 }
                }
            }: '\'openMinute\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.openHour as Integer) < 24 }
                }
            }: '\'openHour\' must be less than 24',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.openMinute as Integer) < 60 }
                }
            }: '\'openMinute\' must be less than 60',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.closeHour as Integer) >= 0 }
                }
            }: '\'closeHour\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.closeMinute as Integer) >= 0 }
                }
            }: '\'closeMinute\' can not be negative',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.closeHour as Integer) < 24 }
                }
            }: '\'closeHour\' must be less than 24',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { (it.closeMinute as Integer) < 60 }
                }
            }: '\'closeMinute\' must be less than 60',
            {
                allValidOpenHours(it.openHours).every { day, List dailyOpeningHours ->
                    dailyOpeningHours.isEmpty() ||
                            dailyOpeningHours.every { Map openPeriod ->
                                def openTime = new SimpleTime(openPeriod.openHour as Integer, openPeriod.openMinute as Integer)
                                def closeTime = new SimpleTime(openPeriod.closeHour as Integer, openPeriod.closeMinute as Integer)
                                openTime < closeTime
                            }
                }
            }: 'Daily venue \'openHours\' must close after opening time'
    ]

    static final UNRECOGNIZED_FACILITY_ERROR_MESSAGE = "Supported facilities are: ${Facility.asStrings()}"
    static final INVALID_FACILITY_STATUS_ERROR_MESSAGE = "The status of a facility can be either 'Y', 'N' or 'UNKNOWN"

    private static final FACILITIES_CHECKS = [
            {
                it.facilities.every { it.key.toLowerCase() in Facility.asStrings() }
            }: UNRECOGNIZED_FACILITY_ERROR_MESSAGE,
            {
                it.facilities.every { it.value.toUpperCase() in ['Y', 'N', 'UNKNOWN']}
            }: INVALID_FACILITY_STATUS_ERROR_MESSAGE
    ]

    private static Set<String> daysOfWeek() {
        DayOfWeek.values().collect { it.toString().toLowerCase() }
    }

    private static Map<String, ?> allValidOpenHours(Map<String, ?> openHours) {
        openHours.subMap(daysOfWeek().intersect(openHours.keySet()))
    }
    private static boolean isInteger(property) {
        if (property instanceof Integer)
            return true

        try {
            Integer.parseInt(property)
        } catch (any) {
            return false
        }
        return true
    }

    private static final ALL_VALIDATION_CHECKS =
            REQUIRED_FIELD_CHECKS +
                    EXPECTED_STRUCTURE_CHECKS +
                    LOCATION_CHECKS +
                    ADDRESS_CHECKS +
                    OPEN_HOURS_CHECKS +
                    FACILITIES_CHECKS

    static final ADDRESS_STRUCTURE_ERROR_MESSAGE = "Expected address to be a map e.g. ['addressLine1': '9 Appold Street', city: 'London'...]"
    static final LOCATION_STRUCTURE_ERROR_MESSAGE = "Expected location to be a map e.g. ['latitude': 1.0, 'longitude': 0.1]"
    static final OPEN_HOURS_STRUCTURE_ERROR_MESSAGE = "Expected openHours to map day to a list of open periods e.g. ['monday': [['openHour': 12, 'openMinute': 0, 'closeHour': 18, 'closeMinute': 30]],\n'tuesday': [['openHour': 8, 'openMinute': 0, 'closeHour': 11, 'closeMinute': 0]],\n...\n]"
    static final FACILITIES_STRUCTURE_ERROR_MESSAGE = "Expected facilities to map to either 'Y' or 'N' e.g. ['wifi': 'N', 'power': 'Y']"
}
