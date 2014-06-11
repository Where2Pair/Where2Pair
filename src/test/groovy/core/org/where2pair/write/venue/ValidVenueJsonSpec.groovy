package org.where2pair.write.venue

import static org.where2pair.write.venue.RawVenueJsonBuilder.rawVenueJson

import spock.lang.Specification

class ValidVenueJsonSpec extends Specification {

    def 'generates venue id'() {
        given:
        def rawVenueJson = rawVenueJson().build()
        def venueJson = new VenueJson(rawVenueJson)
        def validVenueJson = new ValidVenueJson(venueJson)

        when:
        def venueId = validVenueJson.venueId

        then:
        with(venueId) {
            name == venueJson.name
            latitude == venueJson.location.latitude
            longitude == venueJson.location.longitude
            addressLine1 == venueJson.address.addressLine1
        }
    }
}
