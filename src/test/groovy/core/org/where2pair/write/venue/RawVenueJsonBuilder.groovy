package org.where2pair.write.venue

import groovy.json.JsonOutput

class RawVenueJsonBuilder {
    private String name = randomName()
    private Map<String, String> address = [addressLine1: 'addressLine1', addressLine2: 'addressLine2', addressLine3: 'addressLine3', city: 'city',
            postcode: 'postcode', phoneNumber: '01234567890']
    private Map<String, Double> location = [latitude: 1.0, longitude: 0.1]
    private Map<String, List<Map<String, Integer>>> openHours = [monday: [[openHour: 12,
            openMinute: 0, closeHour: 18, closeMinute: 30]]]
    private Map<String, String> facilities = [wifi: 'Y', 'mobile payments': 'N', power: 'UNKNOWN']
    private List<String> missingFields = []

    private Map<String, ?> invalidPropertyValues = [:]

    static RawVenueJsonBuilder rawVenueJson() {
        new RawVenueJsonBuilder()
    }

    static RawVenueJson randomRawVenueJson() {
        new RawVenueJsonBuilder().withName(randomName()).build()
    }

    RawVenueJson build() {
        def jsonMap = createJsonMap()
        removeMissingFields(jsonMap)
        overridePropertiesWithInvalidValues(jsonMap)
        def jsonString = JsonOutput.toJson(jsonMap)
        new RawVenueJson(jsonString)
    }

    def overridePropertiesWithInvalidValues(Map<String, ?> jsonMap) {
        invalidPropertyValues.each { property, value ->
            List<String> propertyParts = property.split('\\.')
            String lastProperty = propertyParts.pop()
            def subCollection = jsonMap
            propertyParts.each {
                subCollection = subCollection[getPropertyForCollection(it, subCollection)]
            }
            subCollection[getPropertyForCollection(lastProperty, subCollection)] = value
        }
    }

    private Map<String, ?> createJsonMap() {
        [
                name: name,
                address: address,
                location: location,
                openHours: openHours,
                facilities: facilities
        ]
    }

    private void removeMissingFields(Map<String, ?> jsonMap) {
        missingFields.each { property ->
            List<String> propertyParts = property.split('\\.')
            String lastProperty = propertyParts.pop()
            def subCollection = jsonMap
            propertyParts.each { subCollection = subCollection[getPropertyForCollection(it, subCollection)] }
            subCollection.remove(lastProperty)
        }
    }

    private getPropertyForCollection(property, collection) {
        (collection instanceof Map) ? property : property as Integer
    }

    RawVenueJsonBuilder withFacilities(Map<String, String> facilities) {
        this.facilities = facilities
        this
    }

    RawVenueJsonBuilder withOpenHours(Map<String, List<Map<String, Integer>>> openHours) {
        this.openHours = openHours
        this
    }

    RawVenueJsonBuilder without(String field) {
        this.missingFields << field
        this
    }

    RawVenueJsonBuilder withInvalidPropertyValue(String property, value) {
        this.invalidPropertyValues[property] = value
        this
    }

    private static String randomName() {
        UUID.randomUUID().toString()
    }

    RawVenueJsonBuilder withName(String name) {
        this.name = name
        this
    }
}
