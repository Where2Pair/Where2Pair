package org.where2pair.venue.find

import groovy.transform.EqualsAndHashCode;
import groovy.transform.ToString;
import static org.where2pair.venue.find.DistanceUnit.parseDistanceUnit

@EqualsAndHashCode
@ToString
class LocationsCriteria {
	List locations
	String distanceUnit

	DistanceUnit getDistanceUnit() {
		parseDistanceUnit(this.@distanceUnit)
	}
		
	int size() {
		locations.size()
	}
	
	boolean isValid() {
		locations.size() in 1..1000 && this.@distanceUnit.toUpperCase() in DistanceUnit.values().collect { it.toString() }
	}
	
	def getErrorResponse() {
		if (isValid()) return null

		String errorMessage
		int status		
		
		if (locations.size() == 0) {
			errorMessage = "Missing locations from the request parameters. I expect a query in the form: nearest?location1=x1,y1&location2=x2,y2..."
			status = 400
		} else if (locations.size() > 100) {
			errorMessage = "Only upto 1000 locations are supported at this time."
			status = 413
		} else {
			errorMessage = "Distance unit '${this.@distanceUnit}' is invalid. Use either 'km' or 'miles' (omitting distanceUnit altogether defaults to 'km')."
			status = 400
		}
		
		[errorMessage, status]
	}
}
