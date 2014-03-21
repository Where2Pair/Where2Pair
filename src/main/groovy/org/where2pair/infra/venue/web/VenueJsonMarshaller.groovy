package org.where2pair.infra.venue.web

import org.where2pair.core.venue.Address
import org.where2pair.core.venue.Coordinates
import org.where2pair.core.venue.Distance
import org.where2pair.core.venue.Venue

class VenueJsonMarshaller {

    OpenHoursJsonMarshaller openHoursJsonMarshaller

    Map asVenueJson(Venue venue) {
        [
                id: venue.id,
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

    List asVenuesWithDistancesJson(List venuesWithDistances) {
        if (!venuesWithDistances)
            return []

        venuesWithDistances.collect {
            List distances = it.distances.collect {
				[location: it.key, distance: distanceAsJson(it.value)]
			}

            [	
				distances: distances, 
				averageDistance: distanceAsJson(it.averageDistance), 
				venue: asVenueJson(it.venue)
			]
        }
    }

	def distanceAsJson(Distance distance) {
		[value: distance.value, unit: distance.unit.toString().toLowerCase()]
	}
	
    Venue asVenue(Map json) {
        new Venue(id: json.id ?: 0,
                name: json.name,
                location: new Coordinates(json.location.latitude, json.location.longitude),
                address: new Address(json.address ?: [:]),
                weeklyOpeningTimes: openHoursJsonMarshaller.asWeeklyOpeningTimes(json.openHours),
                facilities: json.facilities)
    }
}
