package org.where2pair.grails

import grails.converters.JSON

import org.where2pair.Coordinates
import org.where2pair.VenueFinder

import static java.lang.Double.parseDouble

class VenueController {

	VenueFinder venueFinder
	
    def show() { 
		def (lat, lng) = params.'location1'.split(',').collect { parseDouble(it) }
		List venues = venueFinder.findNearestTo(new Coordinates(lat: lat, lng: lng))
		render venues as JSON
	}
}
