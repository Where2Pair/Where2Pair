package org.where2pair.venue.save

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import org.where2pair.venue.VenueRepository
import spock.lang.Specification

class VenueSaveOrUpdaterSpec extends Specification {

    VenueRepository venueRepository = Mock()
    VenueSaveOrUpdater venueWriter = new VenueSaveOrUpdater(venueRepository: venueRepository)

    def "when no matching Venue already exists, then saves new Venue"() {
        given:
        Venue venue = new Venue(name: 'name', location: new Coordinates(1.0, 0.1))
        venueRepository.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1)) >> null
        venueRepository.save(venue) >> 99

        when:
        long result = venueWriter.save(venue)

        then:
        result == 99
    }

    def "when matching Venue is found, then updates existing Venue"() {
        given:
        Venue venue = new Venue(id: 0, name: 'name', location: new Coordinates(1.0, 0.1))
        Venue matchingVenue = new Venue(id: 99)
        venueRepository.findByNameAndCoordinates('name', new Coordinates(1.0, 0.1)) >> matchingVenue

        when:
        long result = venueWriter.save(venue)

        then:
        result == 99
        1 * venueRepository.update({ it == venue && it.id == 99 })
    }
}
