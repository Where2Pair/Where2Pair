package org.where2pair.venue.find

import org.where2pair.venue.Coordinates

class LocationsCriteriaBuilder {

	List locations = []

	static LocationsCriteriaBuilder locationsCriteria() {
		new LocationsCriteriaBuilder()
	}

	LocationsCriteriaBuilder withLocation(lat, lng) {
		locations << new Coordinates(lat,lng)
		this
	}

	LocationsCriteriaBuilder withLocation(coords) {
		locations << coords
		this
	}
	
	LocationsCriteriaBuilder withLocations(locations) {
		this.locations = locations.collect { new Coordinates(it[0],it[1]) }
		this
	}

	LocationsCriteria withDistanceUnit(distanceUnit) {
		new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
	}
}
