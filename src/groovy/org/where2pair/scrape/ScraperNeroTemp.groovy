package org.where2pair.scrape

import groovyx.net.http.ContentType
import groovyx.net.http.RESTClient

String xml = 'http://www.caffenero.com/Sites/www.caffenero.com/neros.xml'.toURL().text

int indexOfNewLine = xml.indexOf('\n')

xml = xml[indexOfNewLine..-1]

println xml

def results = new XmlParser().parseText(xml)

println results

def stores = results.marker.collect {
	def mondayOpen = it.'@mon_o'
	def mondayClose = it.'@mon_c'
	def tuesdayOpen = it.'@tue_o'
	def tuesdayClose = it.'@tue_c'
	def wednesdayOpen = it.'@weds_o'
	def wednesdayClose = it.'@weds_c'
	def thursdayOpen = it.'@thurs_o'
	def thursdayClose = it.'@thurs_c'
	def fridayOpen = it.'@fri_o'
	def fridayClose = it.'@fri_c'
	def saturdayOpen = it.'@satopen'
	def saturdayClose = it.'@satclose'
	def sundayOpen = it.'@sunopen'
	def sundayClose = it.'@sunclose'
	
	//TODO What to do about shit data?
	if (!mondayOpen)
		return
	
	def features = []
	
	if (it.'@Wifi' == 'YES')
		features << 'Wifi'
	if (it.'@DisabledAccess' == 'YES')
		features << 'Disabled access'
	if (it.'@Babychange')
		features << 'Baby changing'
	
	def openHours = [
		'monday': openHours(mondayOpen, mondayClose),
		'tuesday': openHours(tuesdayOpen, tuesdayClose),
		'wednesday': openHours(wednesdayOpen, wednesdayClose),
		'thursday': openHours(thursdayOpen, thursdayClose),
		'friday': openHours(fridayOpen, fridayClose),
		'saturday': saturdayOpen ? openHours(saturdayOpen, saturdayClose) : [],
		'sunday': sundayOpen ? openHours(sundayOpen, sundayClose) : [],
	]
	
	[name: 'Cafe Nero',
		latitude: it.'@lat' as Double,
		longitude: it.'@lng' as Double,
		addressLine1: it.'@address',
		city: it.'@town' ?: it.'@address',
		postcode: it.'@postcode',
		phoneNumber: it.'@telephone',
		openHours: openHours,
		features: features]
}

println "Number of stores: $stores.size"
def where2pair = new RESTClient("http://where2pair.herokuapp.com/")

stores.each {
	println it
	where2pair.post(path: "venue", body: it, requestContentType: ContentType.JSON)
}

def openHours(openTime, closeTime) {
	def (openHour, openMinute) = splitTime(openTime)
	def (closeHour, closeMinute) = splitTime(closeTime)
	
	[[openHour: openHour, openMinute: openMinute, closeHour: closeHour, closeMinute: closeMinute]]
}

def splitTime(time) {
	def parts = time.split (':')
	
	if(parts.size() < 2) {
		println parts.size()
		println parts
	}
	
	def otherParts = parts[1].split (' ')
	
	int hour = Integer.parseInt(parts[0])
	int minute = Integer.parseInt(otherParts[0])
	
	if (otherParts[1] == 'PM')
		hour += 12
		
	[hour, minute]
}