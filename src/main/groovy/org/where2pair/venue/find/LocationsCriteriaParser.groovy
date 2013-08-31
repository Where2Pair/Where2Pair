package org.where2pair.venue.find

import org.where2pair.venue.Coordinates
import static java.lang.Double.parseDouble

class LocationsCriteriaParser {

	LocationsCriteria parse(Map params) {
		List locations = params.findAll { it.key.startsWith('location') }.collect {
			def (lat, lng) = it.value.split(',').collect { parseDouble(it) }
			new Coordinates(lat, lng)
		}
		
		def distanceUnit = params.distanceUnit ?: 'km'
		
		new LocationsCriteria(locations: locations, distanceUnit: distanceUnit)
	}
	
}
