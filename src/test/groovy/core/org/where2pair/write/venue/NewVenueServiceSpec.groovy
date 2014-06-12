package org.where2pair.write.venue

import static groovy.json.JsonOutput.toJson
import static org.where2pair.write.venue.RawVenueJsonBuilder.rawVenueJson
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_CITY_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_LINE_1_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_LINE_2_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_LINE_3_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_POSTCODE_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_PHONE_NUMBER_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.ADDRESS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.FACILITIES_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.INCOMPLETE_OPEN_HOURS_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.INVALID_FACILITY_STATUS_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.LOCATION_LATITUDE_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.LOCATION_LONGITUDE_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.LOCATION_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.NAME_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
import static org.where2pair.write.venue.VenueJsonValidator.UNRECOGNIZED_FACILITY_ERROR_MESSAGE

import com.natpryce.snodge.JsonMutator
import spock.lang.Specification
import spock.lang.Unroll

class NewVenueServiceSpec extends Specification {
    def subscriberA = Mock(NewVenueSavedEventSubscriber)
    def subscriberB = Mock(NewVenueSavedEventSubscriber)
    def newVenueService = new NewVenueServiceFactory().createServiceWithEventSubscribers(subscriberA, subscriberB)

    def 'publishes new venues, assigns and returns id'() {
        given:
        def rawVenueJson = rawVenueJson().build()
        def expectedSaveEvent = NewVenueSavedEvent.create(rawVenueJson)

        when:
        def venueId = newVenueService.save(rawVenueJson)

        then:
        1 * subscriberA.notifyNewVenueSaved(expectedSaveEvent)

        then:
        1 * subscriberB.notifyNewVenueSaved(expectedSaveEvent)

        and:
        venueId == expectedSaveEvent.venueId
    }

    @Unroll
    def 'permits optional fields to be missing (#optionalField)'() {
        given:
        def venueWithoutFacilities = rawVenueJson().without(optionalField).build()

        when:
        newVenueService.save(venueWithoutFacilities)

        then:
        noExceptionThrown()

        where:
        optionalField << ['address.addressLine2', 'address.addressLine3', 'address.phoneNumber', 'facilities']
    }

    @Unroll
    def 'rejects venue json that is not a map'() {
        given:
        def invalidJson = toJson(invalidVenueJson)

        when:
        newVenueService.save(new RawVenueJson(invalidJson))

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
        def invalidVenueJson = rawVenueJson().without(missingProperty).build()

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
        def invalidVenueJson = rawVenueJson().withInvalidPropertyValue(property, value).build()

        when:
        newVenueService.save(invalidVenueJson)

        then:
        def exceptionThrown = thrown InvalidVenueJsonException
        exceptionThrown.message == expectedReason

        where:
        property               | value             | expectedReason
        'name'                 | true              | NAME_STRUCTURE_ERROR_MESSAGE
        'address'              | ['addressLine1']  | ADDRESS_STRUCTURE_ERROR_MESSAGE
        'address.addressLine1' | 12                | ADDRESS_LINE_1_STRUCTURE_ERROR_MESSAGE
        'address.addressLine2' | 34                | ADDRESS_LINE_2_STRUCTURE_ERROR_MESSAGE
        'address.addressLine3' | 56                | ADDRESS_LINE_3_STRUCTURE_ERROR_MESSAGE
        'address.city'         | 78                | ADDRESS_CITY_STRUCTURE_ERROR_MESSAGE
        'address.postcode'     | 90                | ADDRESS_POSTCODE_STRUCTURE_ERROR_MESSAGE
        'address.phoneNumber'  | 1234567890        | ADDRESS_PHONE_NUMBER_STRUCTURE_ERROR_MESSAGE
        'location'             | 'somewhere'       | LOCATION_STRUCTURE_ERROR_MESSAGE
        'location.latitude'    | 'some lat'        | LOCATION_LATITUDE_STRUCTURE_ERROR_MESSAGE
        'location.longitude'   | 'some long'       | LOCATION_LONGITUDE_STRUCTURE_ERROR_MESSAGE
        'openHours'            | 12                | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'openHours.monday'     | [openHour: 12]    | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'openHours.monday.0'   | [12, 0, 15, 0]    | OPEN_HOURS_STRUCTURE_ERROR_MESSAGE
        'facilities'           | ['wifi', 'power'] | FACILITIES_STRUCTURE_ERROR_MESSAGE
    }

    def 'rejects venue json if there are no open hours for Monday-Sunday'() {
        given:
        def invalidVenueJson = rawVenueJson().withOpenHours([invalidDay: []]).build()

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

    @Unroll
    def 'random venue json does not throw unexpected exceptions'() {
        when:
        def exceptionThrownProcessingMutatedJson = captureErrorsProcessing(mutatedVenueJson)

        then:
        !exceptionThrownProcessingMutatedJson

        where:
        mutatedVenueJson << mutatedJson(rawVenueJson().build().payload, 1000)
    }

    List captureErrorsProcessing(String json) {
        try {
            newVenueService.save(new RawVenueJson(json))
        } catch (e) {
            if (!(e instanceof InvalidVenueJsonException))
                return [e, json]
        }
        return []
    }

    private static Iterable<String> mutatedJson(String validJson, int mutantCount) {
        new JsonMutator().forStrings().mutate(validJson, mutantCount)
    }

    RawVenueJson invalidStatus() {
        rawVenueJson().withFacilities([wifi: 'Yeah']).build()
    }

    RawVenueJson unrecognizedFacility() {
        rawVenueJson().withFacilities([Teleporter: 'Y']).build()
    }

    RawVenueJson getOpenHoursWithIncorrectKeys() {
        rawVenueJson().withOpenHours([monday: [[openHuor: 12, openMintue: 0, closeHuor: 15, closeMintue: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithClosedTimeTheSameAsOpenTime() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 12, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithClosedTimeBeforeOpenTime() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenHourLessThan0() {
        rawVenueJson().withOpenHours([monday: [[openHour: -1, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenMinuteLessThan0() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: -1, closeHour: 11, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenHourGreaterThan23() {
        rawVenueJson().withOpenHours([monday: [[openHour: 24, openMinute: 0, closeHour: 11, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenMinuteGreaterThan59() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 60, closeHour: 11, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseHourLessThan0() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: -1, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseMinuteLessThan0() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: -1]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseHourGreaterThan23() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 24, closeMinute: 0]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseMinuteGreaterThan59() {
        rawVenueJson().withOpenHours([monday: [[openHour: 12, openMinute: 0, closeHour: 11, closeMinute: 60]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenHourAsNonInteger() {
        rawVenueJson().withOpenHours([monday: [[openHour  : 'not an integer',
                                                openMinute: 0, closeHour: 18, closeMinute: 30]]]).build()
    }

    RawVenueJson getOpenHoursWithOpenMinuteAsNonInteger() {
        rawVenueJson().withOpenHours([monday: [[openHour  : 12,
                                                openMinute: 'not an integer', closeHour: 18, closeMinute: 30]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseHourAsNonInteger() {
        rawVenueJson().withOpenHours([monday: [[openHour  : 12,
                                                openMinute: 0, closeHour: 'not an integer', closeMinute: 30]]]).build()
    }

    RawVenueJson getOpenHoursWithCloseMinuteAsNonInteger() {
        rawVenueJson().withOpenHours([monday: [[openHour  : 12,
                                                openMinute: 0, closeHour: 18, closeMinute: 'not an integer']]]).build()
    }
}

