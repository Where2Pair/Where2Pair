package org.where2pair.grails

class GormVenueBuilderForIntegration {

	static GormVenue aGormVenue() {
		new GormVenue(name: 'venue name',
			addressLine1: 'addressLine1',
			city: 'city',
			postcode: 'postcode')
	}
	
}
