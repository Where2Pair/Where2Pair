package org.where2pair.read.venue.mappingtojson

import groovy.transform.Immutable
import org.where2pair.read.venue.Venue

@Immutable
class VenueToJsonMapper {

    private OpenHoursToJsonMapper openHoursToJsonMapper = new OpenHoursToJsonMapper()

    Map<String, ?> toJsonStructure(Venue venue) {
        [
                id: venue.id.toString(),
                name: venue.name ?: '',
                location: [
                        latitude: venue.location.lat,
                        longitude: venue.location.lng
                ],
                address: [
                        addressLine1: venue.address.addressLine1 ?: '',
                        addressLine2: venue.address.addressLine2 ?: '',
                        addressLine3: venue.address.addressLine3 ?: '',
                        city: venue.address.city ?: '',
                        postcode: venue.address.postcode ?: '',
                        phoneNumber: venue.address.phoneNumber ?: ''
                ],
                openHours: openHoursToJsonMapper.toJsonStructure(venue.weeklyOpeningTimes),
                facilities: venue.facilities.facilityStatuses.collectEntries {
                    [it.facility.toString(), it.availability.label]
                }
        ]
    }

    List<Map<String, ?>> toJsonStructure(List<Venue> venues) {
        venues.collect { toJsonStructure(it) }
    }
}

