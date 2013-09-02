package org.where2pair.venue.save

import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller
import spock.lang.Specification

class SaveVenueControllerSpec extends Specification {

    SaveVenueController controller = new SaveVenueController()
    VenueSaveOrUpdater venueSaveOrUpdater = Mock()
    VenueJsonMarshaller venueJsonMarshaller = Mock()

    def "should save new venues"() {
        given:
        Map venueJson = [
                name: 'name',
                latitude: 1.0,
                longitude: 0.1,
                addressLine1: 'addressLine1',
                addressLine2: 'addressLine2',
                addressLine3: 'addressLine3',
                city: 'city',
                postcode: 'postcode',
                phoneNumber: '01234567890',
                openHours: [monday: [
                        [openHour: 12, openMinute: 0, closeHour: 18, closeMinute: 30]
                ],
                        tuesday: [
                                [openHour: 8, openMinute: 0, closeHour: 11, closeMinute: 0]
                        ]],
                features: ['wifi', 'mobile payments']
        ]
        Venue venue = new Venue()
        venueJsonMarshaller.asVenue(venueJson) >> venue
        venueSaveOrUpdater.save(venue) >> 99

        when:
        Map response = controller.save(venueJson)

        then:
        response == venueJson + [id: 99]
    }

    def setup() {
        controller.venueSaveOrUpdater = venueSaveOrUpdater
        controller.venueJsonMarshaller = venueJsonMarshaller
    }

}
