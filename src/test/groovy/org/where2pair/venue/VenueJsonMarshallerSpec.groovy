package org.where2pair.venue

import org.where2pair.venue.find.VenueWithDistances
import spock.lang.Specification

import static org.where2pair.venue.ObjectUtils.createVenue
import static org.where2pair.venue.ObjectUtils.createVenueJson

class VenueJsonMarshallerSpec extends Specification {

    Venue venue = createVenue()
    Map venueJson = createVenueJson()
    OpenHoursJsonMarshaller openHoursJsonMarshaller = Mock() {
        asOpenHoursJson(venue.weeklyOpeningTimes) >> venueJson.openHours
        asWeeklyOpeningTimes(venueJson.openHours) >> venue.weeklyOpeningTimes
    }
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

    def "converts VenueWithDistance to json"() {
        given:
        VenueWithDistances venueWithDistance = new VenueWithDistances(venue: venue, distances: [location1: 10, location2: 10])
        Map expectedVenueWithDistanceJson = [
                distance: [average: 10, location1: 10, location2: 10],
                venue: venueJson]

        when:
        List venuesWithDistanceJson = venueJsonMarshaller.asVenuesWithDistanceJson([venueWithDistance])

        then:
        venuesWithDistanceJson == [expectedVenueWithDistanceJson]
    }

    def "ignores average if only one location when converting VenueWithDistance to json"() {
        given:
        VenueWithDistances venueWithDistance = new VenueWithDistances(venue: venue, distances: [location1: 10])
        Map expectedVenueWithDistanceJson = [
                distance: [location1: 10],
                venue: venueJson]

        when:
        List venuesWithDistanceJson = venueJsonMarshaller.asVenuesWithDistanceJson([venueWithDistance])

        then:
        venuesWithDistanceJson == [expectedVenueWithDistanceJson]
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
