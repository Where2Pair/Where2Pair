package org.where2pair

import org.where2pair.Coordinates
import org.where2pair.Venue
import org.where2pair.VenueRepository;
import org.where2pair.VenueSaveOrUpdater;
import org.where2pair.WeeklyOpeningTimesBuilder

import spock.lang.Specification

class VenueControllerSpec extends Specification {

	static final String VENUE_NAME = 'my venue'
    static final long VENUE_ID = 1L
	VenueController controller = new VenueController()
    VenueRepository venueRepository = Mock()
	VenueSaveOrUpdater venueSaveOrUpdater = Mock()
	VenueJsonMarshaller venueJsonMarshaller = Mock()

    def "should show the specified venue"() {
        given:
        Venue venue = new Venue(name: VENUE_NAME)
        Map venueJson = [name: VENUE_NAME]
        venueRepository.get(VENUE_ID) >> venue
        venueJsonMarshaller.asVenueJson(venue) >> venueJson

        when:
        Map response = controller.show(VENUE_ID)

        then:
        response == venueJson
    }

    def "should show 404 if venue not found"() {
        Venue venue = new Venue(name: VENUE_NAME)
        venueRepository.get(VENUE_ID) >> null

        when:
        ErrorResponse response = controller.show(VENUE_ID)

        then:
        response.status == 404
		response.message == "Venue with id $VENUE_ID could not be found"
    }

	def "should show all venues"() {
		given:
        List venues = 100.venues()
		List venuesJson = 100.venuesJson()
		venueRepository.getAll() >> venues
        venueJsonMarshaller.asVenuesJson(venues) >> venuesJson

		when:
		List response = controller.showAll()

		then:
		response == venuesJson
	}
	
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
		controller.venueRepository = venueRepository
		controller.venueSaveOrUpdater = venueSaveOrUpdater
		controller.venueJsonMarshaller = venueJsonMarshaller
		Integer.mixin(VenuesMixin)
	}

	def cleanup() {
		String.metaClass = null
		Integer.metaClass = null
	}

	@Category(Integer)
	static class VenuesMixin {
		List venues() {
			(0..this).collect {
				new Venue(location: new Coordinates(1.0, 0.5),
				weeklyOpeningTimes: new WeeklyOpeningTimesBuilder().build())
			}
		}

        List venuesJson() {
            (0..this).collect {
                [:]
            }
        }
	}
}