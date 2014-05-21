package org.where2pair.write.venue

import groovy.json.JsonSlurper
import org.where2pair.common.venue.Facility
import spock.lang.Specification
import spock.lang.Unroll

import static org.where2pair.read.venue.VenueBuilder.aVenue
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.INVALID_FACILITY_STATUS_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.LOCATION_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.UNRECOGNIZED_FACILITY_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.getFACILITIES_STRUCTURE_ERROR_MESSAGE

class NewVenueServiceSpec extends Specification {

    Map<String, ?> venueJson = aVenue().toJson()
    def subscriberA = Mock(NewVenueSavedEventSubscriber)
    def subscriberB = Mock(NewVenueSavedEventSubscriber)
    def newVenueServiceFactory = new NewVenueServiceFactory()
    def newVenueService = newVenueServiceFactory.createServiceWithEventSubscribers(subscriberA, subscriberB)

    def 'publishes new venues, assigns and returns id'() {
        given:
        def venue = aVenue().build()
        def expectedVenueId = new NewVenueId(venue.name, venue.location, venue.address.addressLine1)

        when:
        def venueId = newVenueService.save(venueJson)

        then:
        1 * subscriberA.notifyNewVenueSaved { NewVenueSavedEvent newVenueSavedEvent ->
            newVenueSavedEvent.newVenue == new NewVenue(venueJson) &&
                    newVenueSavedEvent.venueId == expectedVenueId
        }

        then:
        1 * subscriberB.notifyNewVenueSaved { NewVenueSavedEvent newVenueSavedEvent ->
            newVenueSavedEvent.newVenue == new NewVenue(venueJson) &&
                    newVenueSavedEvent.venueId == expectedVenueId
        }

        and:
        venueId == expectedVenueId
    }

    def 'facilities are optional'() {
        given:
        def venueWithoutFacilities = aVenue().toJson()
        venueWithoutFacilities.remove('facilities')

        when:
        newVenueService.save(venueWithoutFacilities)

        then:
        noExceptionThrown()
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
        property             | value             | expectedReason
        'address'            | ['addressLine1']  | ADDRESS_STRUCTURE_ERROR_MESSAGE
        'location'           | 'somewhere'       | LOCATION_STRUCTURE_ERROR_MESSAGE
        'openHours'          | 12                | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'openHours.monday'   | [openHour: 12]    | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'openHours.monday.0' | [12, 0, 15, 0]    | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'facilities'         | ['wifi', 'power'] | FACILITIES_STRUCTURE_ERROR_MESSAGE
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
        openHoursWithOpenHourAsNonInteger        | '\'openHour\' must be an integer'
        openHoursWithOpenMinuteAsNonInteger      | '\'openMinute\' must be an integer'
        openHoursWithCloseHourAsNonInteger       | '\'closeHour\' must be an integer'
        openHoursWithCloseMinuteAsNonInteger     | '\'closeMinute\' must be an integer'
    }

    @Unroll
    def 'rejects venue json if facilities have invalid values'() {
        when:
        newVenueService.save(invalidJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == expectedReason

        where:
        invalidJson            | expectedReason
        unrecognizedFacility() | UNRECOGNIZED_FACILITY_ERROR_MESSAGE
        invalidStatus()        | INVALID_FACILITY_STATUS_ERROR_MESSAGE
    }

    Map<String, ?> invalidStatus() {
        aVenue().toJson() + [facilities: [(Facility.values()[0].toString()): 'Yeah']]
    }

    Map<String, ?> unrecognizedFacility() {
        aVenue().toJson() + [facilities: [Teleporter: 'Y']]
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

    Map<String, ?> getOpenHoursWithOpenHourAsNonInteger() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 'not an integer', openMinute: 0, closeHour: 11, closeMinute: 60]]]]
    }

    Map<String, ?> getOpenHoursWithOpenMinuteAsNonInteger() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 'not an integer', closeHour: 11, closeMinute: 60]]]]
    }

    Map<String, ?> getOpenHoursWithCloseHourAsNonInteger() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 'not an integer', closeMinute: 60]]]]
    }

    Map<String, ?> getOpenHoursWithCloseMinuteAsNonInteger() {
        aVenue().toJson() + [openHours: [monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 'not an integer']]]]
    }
}
