package org.where2pair.infra.venue.web

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.*
import spock.lang.Specification

import static VenueDetailsBuilder.venueDetails
import static org.where2pair.core.venue.read.DistanceUnit.KM

class VenueJsonMarshallerSpec extends Specification {

    //When Venue serializeto Json with Id

    Venue venue = VenueBuilder.aVenue().build()
    Map venueJson = venueDetails().toJson()
//    OpenHoursJsonMarshaller openHoursJsonMarshaller = Mock() {
//        asOpenHoursJson(venue.weeklyOpeningTimes) >> venueJson.openHours
//        asWeeklyOpeningTimes(venueJson.openHours) >> venue.weeklyOpeningTimes
//    }
    VenueJsonMarshaller venueJsonMarshaller = new VenueJsonMarshaller(
            openHoursJsonMarshaller: openHoursJsonMarshaller)

    def "converts Venue to json"() {
        when:
        Map result = venueJsonMarshaller.asVenueJson(venue)

        then:
        result == venueJson
    }

    def "converts Venues to json"() {
        when:
        List result = venueJsonMarshaller.asVenuesJson([venue])

        then:
        result == [venueJson]
    }

    def "converts VenueWithDistances to json"() {
        given:
        def location1 = new Coordinates(1.0, 0.1)
        def location2 = new Coordinates(2.0, 0.2)
        VenueWithDistances venueWithDistances = new VenueWithDistances(venue: venue, distances: [
                (location1): new Distance(value: 10, unit: KM),
                (location2): new Distance(value: 30, unit: KM)])
        List expectedVenuesWithDistancesJson = [[
                distances: [
                        [
                                location: location1,
                                distance: [
                                        value: 10.0,
                                        unit: "km"
                                ]
                        ],
                        [
                                location: location2,
                                distance: [
                                        value: 30.0,
                                        unit: "km"
                                ]
                        ]
                ],
                averageDistance: [
                        value: 20.0,
                        unit: "km"
                ],
                venue: venueJson]]

        when:
        List venuesWithDistanceJson = venueJsonMarshaller.asVenuesWithDistancesJson([venueWithDistances])

        then:
        venuesWithDistanceJson == expectedVenuesWithDistancesJson
    }

    def "renders null string values as empty strings"() {
        given:
        Venue venue = new Venue(
                address: new Address(),
                weeklyOpeningTimes: new WeeklyOpeningTimes(),
                location: new Coordinates(0, 0))

        when:
        Map venueJson = venueJsonMarshaller.asVenueJson(venue)

        then:
        venueJson.name == ""
        venueJson.address.addressLine1 == ""
        venueJson.address.addressLine2 == ""
        venueJson.address.addressLine3 == ""
        venueJson.address.city == ""
        venueJson.address.postcode == ""
        venueJson.address.phoneNumber == ""
    }

    def "converts json to Venue"() {
        when:
        Venue result = venueJsonMarshaller.asVenue(venueJson)

        then:
        result == venue
    }

    def "converts json to Venue when address is null"() {
        given:
        venueJson.address = null
        venue.address = new Address()

        when:
        Venue result = venueJsonMarshaller.asVenue(venueJson)

        then:
        result == venue
    }

    def "converts json to Venue when id is null"() {
        given:
        venueJson.id = null
        venue.id = 0

        when:
        Venue result = venueJsonMarshaller.asVenue(venueJson)

        then:
        result == venue
    }

}