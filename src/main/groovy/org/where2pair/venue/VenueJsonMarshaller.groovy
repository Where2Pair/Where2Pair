package org.where2pair.venue

class VenueJsonMarshaller {

    OpenHoursJsonMarshaller openHoursJsonMarshaller

    Map asVenueJson(Venue venue) {
        [
                id: venue.id,
                name: venue.name ?: '',
                latitude: venue.location.lat,
                longitude: venue.location.lng,
                address: [
                        addressLine1: venue.address.addressLine1 ?: '',
                        addressLine2: venue.address.addressLine2 ?: '',
                        addressLine3: venue.address.addressLine3 ?: '',
                        city: venue.address.city ?: '',
                        postcode: venue.address.postcode ?: '',
                        phoneNumber: venue.address.phoneNumber ?: ''
                ],
                openHours: openHoursJsonMarshaller.asOpenHoursJson(venue.weeklyOpeningTimes),
                facilities: venue.facilities.collect()
        ]
    }

    List asVenuesJson(List venues) {
        if (!venues)
            return []

        venues.collect { Venue venue ->
            asVenueJson(venue)
        }
    }

    List asVenuesWithDistanceJson(List venuesWithDistance) {
        if (!venuesWithDistance)
            return []

        venuesWithDistance.collect {
            Map distances = it.distances
            if (distances.size() > 1) distances['average'] = it.averageDistance

            [distance: distances.sort(), venue: asVenueJson(it.venue)]
        }
    }

    Venue asVenue(Map json) {
        new Venue(id: json.id ?: 0,
                name: json.name,
                location: new Coordinates(json.latitude, json.longitude),
                address: new Address(json.address ?: [:]),
                weeklyOpeningTimes: openHoursJsonMarshaller.asWeeklyOpeningTimes(json.openHours),
                facilities: json.facilities)
    }
}
