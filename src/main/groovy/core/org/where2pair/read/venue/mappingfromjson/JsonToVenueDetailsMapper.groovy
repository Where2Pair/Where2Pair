package org.where2pair.read.venue.mappingfromjson

import org.where2pair.common.venue.Coordinates
import org.where2pair.read.venue.Address
import org.where2pair.read.venue.FacilityStatus
import org.where2pair.read.venue.FacilityStatuses
import org.where2pair.read.venue.VenueDetails
import org.where2pair.read.venue.WeeklyOpeningTimes

import static org.where2pair.read.venue.FacilityStatuses.facilityStatusesFor

class JsonToVenueDetailsMapper {

    private final JsonToOpenHoursMapper jsonToOpenHoursMapper = new JsonToOpenHoursMapper()

    VenueDetails toVenueDetails(Map<String, ?> json) {
        String name = json.name
        Coordinates location = coordinatesFrom(json.location)
        Address address = addressFrom(json.address)
        WeeklyOpeningTimes weeklyOpeningTimes = jsonToOpenHoursMapper.asWeeklyOpeningTimes(json.openHours)
        FacilityStatuses facilityStatuses = facilityStatusesFor(asFacilityStatuses(json.facilities))
        new VenueDetails(name, location, address, weeklyOpeningTimes, facilityStatuses)
    }

    private Set<FacilityStatus> asFacilityStatuses(Map<String, String> json) {
        json.collect { facility, status ->
            new FacilityStatus(facility: facility.toUpperCase(), status: FacilityStatus.Status.fromString(status))
        }
    }

    private Coordinates coordinatesFrom(Map<String, ?> json) {
        double latitude = json.latitude
        double longitude = json.longitude
        new Coordinates(latitude, longitude)
    }

    private Address addressFrom(Map<String, String> json) {
        String addressLine1 = json.addressLine1
        String addressLine2 = json.addressLine2
        String addressLine3 = json.addressLine3
        String postcode = json.postcode
        String city = json.city
        String phoneNumber = json.phoneNumber
        new Address(addressLine1, addressLine2, addressLine3, city, postcode, phoneNumber)
    }
}