package org.where2pair.infra.venue.read

import org.where2pair.core.venue.common.Coordinates
import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.read.VenueRepository
import org.where2pair.core.venue.read.mappingtojson.WeeklyOpeningTimesBuilder
import org.where2pair.infra.venue.read.ShowVenueController
import spock.lang.Specification

class ShowVenueControllerSpec extends Specification {

    static final String VENUE_NAME = 'my venue'
    static final String VENUE_ID = '1'
    ShowVenueController controller = new ShowVenueController()
    VenueRepository venueRepository = Mock()

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
        def response = controller.show(VENUE_ID)

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

    def setup() {
        controller.venueRepository = venueRepository
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