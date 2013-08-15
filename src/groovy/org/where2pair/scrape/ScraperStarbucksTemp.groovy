package org.where2pair.scrape

import org.apache.catalina.Store;

import groovy.json.JsonSlurper
import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

def results = 'https://openapi.starbucks.com/location/v1/stores?&callback=jQuery17206076719672419131_1370202711616&radius=50&limit=100&brandCode=SBUX&latLng=51.520547%2C-0.082103&apikey=7b35m595vccu6spuuzu2rjh4&_=1370202720762'.toURL().text

results = results.replaceAll('jQuery17206076719672419131_1370202711616', '')[1..-1]

def map = new JsonSlurper().parseText(results)

def (myLat, myLong) = [51.51817, -0.084286]

def stores = map.items.collect { 
    
    def store = it.store
	def address = store.address
    def regularHours = store.regularHours
    def openHours = regularHours.collectEntries {
        String[] openTimes = it.value.openTime.split(':')
        String[] closeTimes = it.value.closeTime.split(':')
        [(it.key): [[openHour: openTimes[0], openMinute: openTimes[1], 
            closeHour: closeTimes[0], closeMinute: closeTimes[1]]]]
    }
	
	def features = store.features.findResults {
		if (it.name == 'Wireless Hotspot')
			return 'Wifi'
		if (it.name == 'Mobile Payment')
			return 'Mobile payments'
			
		null
	}
	
	List addressLines = []
	
	if (address.streetAddressLine1)
		addressLines << address.streetAddressLine1
	if (address.streetAddressLine2)
		addressLines << address.streetAddressLine2
	if (address.streetAddressLine3)
		addressLines << address.streetAddressLine3
	
	while (addressLines.size() < 3)
		addressLines << ''
		
    [name: 'Starbucks', 
        longitude: store.coordinates.longitude, 
        latitude: store.coordinates.latitude,
		addressLine1: addressLines[0],
		addressLine2: addressLines[1],
		addressLine3: addressLines[2],
		city: address.city,
		postcode: address.postalCode,
		phoneNumber: store.phoneNumber,
        openHours: openHours,
		features: features]
}

println "Number of stores: $stores.size"
def where2pair = new RESTClient("http://where2pair.herokuapp.com/")

stores.each { 
	println it 
	where2pair.post(path: "venue", body: it, requestContentType: ContentType.JSON)
}