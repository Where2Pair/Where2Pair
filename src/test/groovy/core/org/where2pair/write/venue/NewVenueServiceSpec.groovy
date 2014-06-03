package org.where2pair.write.venue

import static groovy.json.JsonOutput.toJson
import static org.where2pair.write.venue.VenueJsonBuilder.venueJson
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.FACILITIES_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.INCOMPLETE_OPEN_HOURS_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.INVALID_FACILITY_STATUS_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.LOCATION_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.UNRECOGNIZED_FACILITY_ERROR_MESSAGE

import spock.lang.Specification
import spock.lang.Unroll

class NewVenueServiceSpec extends Specification {

    def venueJson = venueJson().build()
    def subscriberA = Mock(NewVenueSavedEventSubscriber)
    def subscriberB = Mock(NewVenueSavedEventSubscriber)
    def newVenueServiceFactory = new NewVenueServiceFactory()
    def newVenueService = newVenueServiceFactory.createServiceWithEventSubscribers(subscriberA, subscriberB)

    def 'publishes new venues, assigns and returns id'() {
        given:
        def expectedVenueId = new NewVenueId(venueJson.jsonMap.name,
                venueJson.jsonMap.location.latitude,
                venueJson.jsonMap.location.longitude,
                venueJson.jsonMap.address.addressLine1)

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
        def venueWithoutFacilities = venueJson().without('facilities').build()

        when:
        newVenueService.save(venueWithoutFacilities)

        then:
        noExceptionThrown()
    }

    def 'rejects venue json that is not a map'() {
        given:
        def invalidJson = toJson(invalidVenueJson)

        when:
        newVenueService.save(new VenueJson(invalidJson))

        then:
        def exception = thrown(InvalidVenueJsonException)
        exception.message == expectedErrorMessage

        where:
        invalidVenueJson | expectedErrorMessage
        []               | 'Venue json not in the expected format'
        ''               | 'Venue json not in the expected format'
    }

    @Unroll
    def 'rejects venue json missing "#missingProperty"'() {
        given:
        def invalidVenueJson = venueJson().without(missingProperty).build()

        when:
        newVenueService.save(invalidVenueJson)

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
        def invalidVenueJson = venueJson().withInvalidPropertyValue(property, value).build()

        when:
        newVenueService.save(invalidVenueJson)

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
        def invalidVenueJson = venueJson().withOpenHours([invalidDay: []]).build()

        when:
        newVenueService.save(invalidVenueJson)

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
        openHoursWithIncorrectKeys               | INCOMPLETE_OPEN_HOURS_ERROR_MESSAGE
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

    VenueJson invalidStatus() {
        venueJson().withFacilities([wifi: 'Yeah']).build()
    }

    VenueJson unrecognizedFacility() {
        venueJson().withFacilities([Teleporter: 'Y']).build()
    }

    VenueJson getOpenHoursWithIncorrectKeys() {
        venueJson().withOpenHours([monday: [[openHuor: 12, openMintue: 0, closeHuor: 15, closeMintue: 0]]]).build()
    }

    VenueJson getOpenHoursWithClosedTimeTheSameAsOpenTime() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 12, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithClosedTimeBeforeOpenTime() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithOpenHourLessThan0() {
        venueJson().withOpenHours([monday: [[openHour: -1, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithOpenMinuteLessThan0() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: -1, closeHour: 11, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithOpenHourGreaterThan23() {
        venueJson().withOpenHours([monday: [[openHour: 24, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithOpenMinuteGreaterThan59() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 60, closeHour: 11, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithCloseHourLessThan0() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: -1, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithCloseMinuteLessThan0() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: -1]]]).build()
    }

    VenueJson getOpenHoursWithCloseHourGreaterThan23() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 24, closeMinute: 0]]]).build()
    }

    VenueJson getOpenHoursWithCloseMinuteGreaterThan59() {
        venueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 60]]]).build()
    }

    VenueJson getOpenHoursWithOpenHourAsNonInteger() {
        venueJson().withOpenHours([monday: [[openHour  : 'not an integer',
                                             openMinute: 0, closeHour: 18, closeMinute: 30]]]).build()
    }

    VenueJson getOpenHoursWithOpenMinuteAsNonInteger() {
        venueJson().withOpenHours([monday: [[openHour  : 12,
                                             openMinute: 'not an integer', closeHour: 18, closeMinute: 30]]]).build()
    }

    VenueJson getOpenHoursWithCloseHourAsNonInteger() {
        venueJson().withOpenHours([monday: [[openHour  : 12,
                                             openMinute: 0, closeHour: 'not an integer', closeMinute: 30]]]).build()
    }

    VenueJson getOpenHoursWithCloseMinuteAsNonInteger() {
        venueJson().withOpenHours([monday: [[openHour  : 12,
                                             openMinute: 0, closeHour: 18, closeMinute: 'not an integer']]]).build()
    }
}

