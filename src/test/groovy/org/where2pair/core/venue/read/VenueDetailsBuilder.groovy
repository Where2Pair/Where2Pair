package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.CoordinatesBuilder
import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.mappingtojson.OpenHoursToJsonMapper
import org.where2pair.core.venue.read.mappingtojson.WeeklyOpeningTimesBuilder

import static org.where2pair.core.venue.read.DayOfWeek.MONDAY
import static org.where2pair.core.venue.read.DayOfWeek.TUESDAY
import static org.where2pair.core.venue.read.Facility.MOBILE_PAYMENTS
import static org.where2pair.core.venue.read.Facility.WIFI
import static org.where2pair.core.venue.read.FacilityStatus.AVAILABLE
import static org.where2pair.core.venue.read.FacilityStatus.UNAVAILABLE

class VenueDetailsBuilder {
    private String name = 'venue name'
    private Address address = new Address('addressLine1', 'addressLine2', 'addressLine3', 'city', 'postcode', '01234567890')
    private Coordinates location = new Coordinates(1.0, 0.1)
    private OpenHoursToJsonMapper openHoursToJsonMapper = new OpenHoursToJsonMapper()
    private WeeklyOpeningTimesBuilder weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()
    private Map<Facility, FacilityStatus> facilities

    private VenueDetailsBuilder() {
        weeklyOpeningTimesBuilder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
        weeklyOpeningTimesBuilder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
        withFacilities(WIFI, MOBILE_PAYMENTS)
    }

    static VenueDetailsBuilder venueDetails() {
        new VenueDetailsBuilder()
    }

    VenueDetailsBuilder withName(String name) {
        this.name = name
        this
    }

    VenueDetailsBuilder withAddress(Address address) {
        this.address = address
        this
    }

    VenueDetailsBuilder withLocation(Coordinates location) {
        this.location = location
        this
    }

    VenueDetailsBuilder withLocation(CoordinatesBuilder coordinatesBuilder) {
        withLocation(coordinatesBuilder.build())
    }

    VenueDetailsBuilder withOpenPeriod(OpenPeriodBuilder openPeriodBuilder) {
        this.weeklyOpeningTimesBuilder.addOpenPeriod(openPeriodBuilder.dayOfWeek, openPeriodBuilder.openTime, openPeriodBuilder.closeTime)
        this
    }

    VenueDetailsBuilder withFacilities(Facility... facilities) {
        this.facilities = Facility.values().collectEntries {
            [it, (it in facilities) ? AVAILABLE : UNAVAILABLE]
        }
        this
    }

    VenueDetailsBuilder withNoFacilities() {
        withFacilities()
    }

    VenueDetails build() {
        new VenueDetails(
                name: name,
                location: location,
                address: address,
                weeklyOpeningTimes: weeklyOpeningTimesBuilder.build(),
                facilities: facilities
        )
    }

    Map<String, ?> toJson() {
        [
                name: name ?: '',
                location: [
                        latitude: location.lat,
                        longitude: location.lng
                ],
                address: [
                        addressLine1: address.addressLine1 ?: '',
                        addressLine2: address.addressLine2 ?: '',
                        addressLine3: address.addressLine3 ?: '',
                        city: address.city ?: '',
                        postcode: address.postcode ?: '',
                        phoneNumber: address.phoneNumber ?: ''
                ],
                openHours: openHoursToJsonMapper.asOpenHoursJson(weeklyOpeningTimesBuilder.build()),
                facilities: facilities.collect()
        ]
    }
}
