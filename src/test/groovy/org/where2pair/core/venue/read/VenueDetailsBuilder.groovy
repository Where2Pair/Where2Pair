package org.where2pair.core.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.common.SimpleTime
import org.where2pair.core.venue.read.mappingtojson.OpenHoursToJsonMapper
import org.where2pair.core.venue.read.mappingtojson.WeeklyOpeningTimesBuilder

import static org.where2pair.core.venue.read.DayOfWeek.MONDAY
import static org.where2pair.core.venue.read.DayOfWeek.TUESDAY

class VenueDetailsBuilder {
    private String name = 'venue name'
    private Address address = new Address('addressLine1', 'addressLine2', 'addressLine3', 'city', 'postcode', '01234567890')
    private Coordinates location = new Coordinates(1.0, 0.1)
    private WeeklyOpeningTimesBuilder weeklyOpeningTimesBuilder = new WeeklyOpeningTimesBuilder()
    private LinkedHashSet<String> facilities = ['wifi', 'mobile payments']
    private OpenHoursToJsonMapper openHoursToJsonMapper = new OpenHoursToJsonMapper()

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
