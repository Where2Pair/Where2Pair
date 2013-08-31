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
			features: venue.features.collect()
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
		
		List venues = venuesWithDistance.venue
		List distances = venuesWithDistance.distance
		List venuesMap = asVenuesJson(venues)
		[venuesMap, distances].transpose().collect { Map venueJson, double distance -> 
			[distance: distance, venue: venueJson]
		}
	}
	
	Venue asVenue(Map json) {
		new Venue(id: json.id ?: 0,
			name: json.name,
			location: new Coordinates(json.latitude, json.longitude),
			address: new Address(json.address ?: [:]),
			weeklyOpeningTimes: openHoursJsonMarshaller.asWeeklyOpeningTimes(json.openHours),
			features: json.features)
	}
}
