package org.where2pair.read.venue

import static org.where2pair.common.venue.Facility.MOBILE_PAYMENTS
import static org.where2pair.common.venue.Facility.WIFI
import static org.where2pair.common.venue.FacilityAvailability.AVAILABLE
import static org.where2pair.read.venue.DayOfWeek.MONDAY
import static org.where2pair.read.venue.DayOfWeek.TUESDAY
import static org.where2pair.read.venue.FacilityStatuses.facilityStatusesFor

import org.where2pair.common.venue.Facility
import org.where2pair.common.venue.SimpleTime
import org.where2pair.read.venue.mappingtojson.OpenHoursToJsonMapper
import org.where2pair.read.venue.mappingtojson.WeeklyOpeningTimesBuilder

class VenueDetailsBuilder {
    private String name = 'venue name'
    private Address address = new Address('addressLine1', 'addressLine2', 'addressLine3', 'city',
            'postcode', '01234567890')
    private Coordinates location = new Coordinates(1.0, 0.1)
    private OpenHoursToJsonMapper openHoursToJsonMapper = new OpenHoursToJsonMapper()
    private WeeklyOpeningTimesBuilder weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()
    private FacilityStatuses facilityStatuses = asAvailableFacilities(WIFI, MOBILE_PAYMENTS)

    private VenueDetailsBuilder() {
        weeklyOpeningTimesBuilder.addOpenPeriod(MONDAY, new SimpleTime(12, 0), new SimpleTime(18, 30))
        weeklyOpeningTimesBuilder.addOpenPeriod(TUESDAY, new SimpleTime(8, 0), new SimpleTime(11, 0))
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
        this.weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()
        this.weeklyOpeningTimesBuilder.addOpenPeriod(
                openPeriodBuilder.dayOfWeek, openPeriodBuilder.openTime, openPeriodBuilder.closeTime)
        this
    }

    VenueDetailsBuilder withFacilities(Facility... facilities) {
        this.facilityStatuses = asAvailableFacilities(facilities)
        this
    }

    VenueDetailsBuilder withNoFacilities() {
        withFacilities()
    }

    private FacilityStatuses asAvailableFacilities(Facility... facilities) {
        Set<FacilityStatus> availableFacilities = facilities.collect {
            new FacilityStatus(it, AVAILABLE)
        }
        facilityStatusesFor(availableFacilities)
    }

    VenueDetails build() {
        new VenueDetails(
                name: name,
                location: location,
                address: address,
                weeklyOpeningTimes: weeklyOpeningTimesBuilder.build(),
                facilities: facilityStatuses
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
                openHours: openHoursToJsonMapper.toJsonStructure(weeklyOpeningTimesBuilder.build()),
                facilities: facilityStatuses.facilityStatuses.collectEntries {
                    [it.facility.toString(), it.availability.label ]
                }
        ]
    }
}

