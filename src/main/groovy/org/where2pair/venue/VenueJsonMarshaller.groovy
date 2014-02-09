package org.where2pair.venue

import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

class VenueJsonMarshaller {

    OpenHoursJsonMarshaller openHoursJsonMarshaller

    Map asVenueJson(Venue venue) {
        [
                id: venue.id,
                name: venue.name ?: '',
                location: [
						latitude: venue.location.latitude,
						longitude: venue.location.longitude
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
        def openHours = openHoursJsonMarshaller.asWeeklyOpeningTimes(json.openHours)
        new Venue(id: json.id ?: 0,
                name: json.name,
                location: new Coordinates(json.location.latitude, json.location.longitude),
                address: new Address(json.address ?: [:]),
                weeklyOpeningTimes: openHours,
                facilities: json.facilities)
    }

    Venue asVenue(String json) {
        asVenue((Map) new JsonSlurper().parseText(json))
    }

    List asVenues(String json) {
        List venuesJson = new JsonSlurper().parseText(json)

        venuesJson.collect({
            asVenue(it)
        })
    }

    String asVenueJsonString(Venue venue) {
        def json = new JsonBuilder()
        json id: venue.id,
                name: venue.name,
                location: [latitude: venue.location.latitude,
                        longitude: venue.location.longitude],
                address: [
                        addressLine1: venue.address.addressLine1,
                        addressLine2: venue.address.addressLine2,
                        addressLine3: venue.address.addressLine3,
                        city: venue.address.city,
                        postcode: venue.address.postcode,
                        phoneNumber: venue.address.phoneNumber ],
                openHours: venue.weeklyOpeningTimes.weeklyOpeningTimes.collectEntries {key, value ->
                    [(key): value.openPeriods.collect { openPeriod ->
                            [openHour: openPeriod.start.hour, openMinute: openPeriod.start.minute,
                            closeHour: openPeriod.end.hour, closeMinute: openPeriod.end.minute]
                          }
                    ]},
                facilities: venue.facilities

        json.toString()
    }
}
