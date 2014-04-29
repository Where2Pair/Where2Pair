package org.where2pair.core.venue.write

import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.read.Venue
import spock.lang.Specification
import spock.lang.Unroll

import static org.where2pair.core.venue.read.VenueBuilder.aVenue


class NewVenueServiceTest extends Specification {

    Map<String, ?> venueJson = aVenue().toJson()
    NewVenueSavedEventPublisher newVenueSavedEventPublisher = Mock()
    NewVenueService newVenueService = new NewVenueService(newVenueSavedEventPublisher: newVenueSavedEventPublisher)

    def 'publishes new venues, assigns and returns id'() {
        given:
        Venue venue = aVenue().build()
        VenueId expectedVenueId = new VenueId(venue.name, venue.location, venue.address.addressLine1)

        when:
        println venueJson
        VenueId venueId = newVenueService.save(venueJson)

        then:
        1 * newVenueSavedEventPublisher.publish { NewVenueSavedEvent newVenueSavedEvent ->
            newVenueSavedEvent.newVenue == new NewVenue(venueJson) &&
                    newVenueSavedEvent.venueId == expectedVenueId
        }
        venueId == expectedVenueId
    }

    @Unroll
    def 'rejects venue json missing "#missingProperty"'() {
        given:
        Map<String, ?> invalidJson = venueJsonMissing(missingProperty)

        when:
        newVenueService.save(invalidJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == expectedReason

        where:
        missingProperty        | expectedReason
        'name'                 | 'Venue \'name\' must be provided'
        'location'             | 'Venue \'location\' must be provided'
        'location.latitude'    | 'Venue \'location.latitude\' must be provided'
        'location.longitude'   | 'Venue \'location.longitude\' must be provided'
        'address'              | 'Venue \'address\' must be provided'
        'address.addressLine1' | 'Venue \'address.addressLine1\' must be provided'
        'address.city'         | 'Venue \'address.city\' must be provided'
        'address.postcode'     | 'Venue \'address.postcode\' must be provided'
        'openHours'            | 'Venue \'openHours\' must be provided'
    }

    @Unroll
    def 'rejects venue json if "#property" property is not of the correct type'() {
        given:
        Map<String, ?> invalidJson = overrideValidVenueJsonWith(property, value)

        when:
        newVenueService.save(invalidJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == expectedReason

        where:
        property             | value            | expectedReason
        'address'            | ['addressLine1'] | 'Expected address to be a map e.g. [addressLine1: \'9 Appold Street\', city: \'London\'...]'
        'location'           | 'somewhere'      | 'Expected location to be a map e.g. [latitude: 1.0, longitude: 0.1]'
        'openHours'          | 12               | 'Expected openHours to map day to a list of open periods e.g. [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],\ntuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],\n...\n]'
        'openHours.monday'   | [openHour: 12]   | 'Expected openHours to map day to a list of open periods e.g. [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],\ntuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],\n...\n]'
        'openHours.monday.0' | [12, 0, 15, 0]   | 'Expected openHours to map day to a list of open periods e.g. [monday: [[openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]],\ntuesday: [[openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]],\n...\n]'
    }

    def 'rejects venue json if there are no open hours for Monday-Sunday'() {
        given:
        Map<String, ?> invalidJson = venueJson + [openHours: [invalidDay: []]]

        when:
        newVenueService.save(invalidJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == 'Venue \'openHours\' must contain at least one open day (monday-sunday)'
    }

    @Unroll
    def 'rejects venue json if daily open hours has invalid values'() {
        when:
        newVenueService.save(invalidJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == expectedReason

        where:
        invalidJson                              | expectedReason
        openHoursWithIncorrectKeys               | 'Daily venue \'openHours\' must contain \'openHour\', \'openMinute\', \'closeHour\' and \'closeHour\''
        openHoursWithClosedTimeTheSameAsOpenTime | 'Daily venue \'openHours\' must close after opening time'
        openHoursWithClosedTimeBeforeOpenTime    | 'Daily venue \'openHours\' must close after opening time'
        openHoursWithOpenHourLessThan0           | '\'openHour\' can not be negative'
        openHoursWithOpenMinuteLessThan0         | '\'openMinute\' can not be negative'
        openHoursWithOpenHourGreaterThan23       | '\'openHour\' must be less than 24'
        openHoursWithOpenMinuteGreaterThan59     | '\'openMinute\' must be less than 60'
        openHoursWithCloseHourLessThan0          | '\'closeHour\' can not be negative'
        openHoursWithCloseMinuteLessThan0        | '\'closeMinute\' can not be negative'
        openHoursWithCloseHourGreaterThan23      | '\'closeHour\' must be less than 24'
        openHoursWithCloseMinuteGreaterThan59    | '\'closeMinute\' must be less than 60'
    }

    Map<String, ?> venueJsonMissing(String property) {
        Map<String, ?> venueJson = aVenue().toJson()
        List<String> propertyParts = property.split('\\.')
        String lastProperty = propertyParts.pop()
        def subCollection = venueJson
        propertyParts.each { subCollection = subCollection[getPropertyForCollection(it, subCollection)] }
        subCollection.remove(lastProperty)
        venueJson
    }

    Map<String, ?> overrideValidVenueJsonWith(String property, Object value) {
        Map<String, ?> venueJson = aVenue().toJson()
        List<String> propertyParts = property.split('\\.')
        String lastProperty = propertyParts.pop()
        def subCollection = venueJson
        propertyParts.each {
            subCollection = subCollection[getPropertyForCollection(it, subCollection)]
        }
        subCollection[getPropertyForCollection(lastProperty, subCollection)] = value
        venueJson
    }

    def getPropertyForCollection(property, collection) {
        (collection instanceof Map) ? property : property as Integer
    }

    Map<String, ?> getOpenHoursWithIncorrectKeys() {
        aVenue().toJson() + [openHours: [monday: [[openHuor: 12, openMintue: 0, closeHuor: 15, closeMintue: 0]]]]
    }

    Map<String, ?> getOpenHoursWithClosedTimeTheSameAsOpenTime() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 12, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithClosedTimeBeforeOpenTime() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithOpenHourLessThan0() {
        aVenue().toJson() + [openHours: [monday: [[openHour: -1, openMinute: 0, closeHour: 11, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithOpenMinuteLessThan0() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: -1, closeHour: 11, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithOpenHourGreaterThan23() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 24, openMinute: 0, closeHour: 11, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithOpenMinuteGreaterThan59() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 60, closeHour: 11, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithCloseHourLessThan0() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: -1, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithCloseMinuteLessThan0() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: -1]]]]
    }

    Map<String, ?> getOpenHoursWithCloseHourGreaterThan23() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 24, closeMinute: 0]]]]
    }

    Map<String, ?> getOpenHoursWithCloseMinuteGreaterThan59() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 60]]]]
    }
}
