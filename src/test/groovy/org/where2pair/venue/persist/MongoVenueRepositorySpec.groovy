package org.where2pair.venue.persist

import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller
import spock.lang.Specification

import static org.where2pair.venue.ObjectUtils.*
import static org.where2pair.venue.persist.MongoVenueRepository.VENUE_COLLECTION

class MongoVenueRepositorySpec extends Specification {

    VenueJsonMarshaller venueJsonMarshaller = Mock()
    MongoService mongoService = Mock()
    MongoVenueRepository mongoVenueRepository = new MongoVenueRepository(venueJsonMarshaller: venueJsonMarshaller, mongoService: mongoService)

    def 'uses the mongo service to retrieve all venues'() {
        given:
        def expectedVenues = [createVenue(), createDifferentVenue()]
        mongoService.find(VENUE_COLLECTION) >> '[{id:1},{id:2}]'
        venueJsonMarshaller.asVenues('[{id:1},{id:2}]') >> expectedVenues

        when:
        def venues = mongoVenueRepository.getAll()

        then:
        venues == expectedVenues
    }

    def 'retrieves the venue by id'() {
        given:
        def venueJson = '{"id" : "1234", facilities:"wifi,coffee"}'
        def expectedVenue = createVenue()
        mongoService.findById(VENUE_COLLECTION, '1234') >> venueJson
        venueJsonMarshaller.asVenue(venueJson) >> expectedVenue

        when:
        def venue = mongoVenueRepository.get(1234L)

        then:
        venue == expectedVenue

    }

    def 'finds venues by name and coordinates'() {
        given:
        Venue venue1 = createVenue()
        String venue1Json = createVenueJson().toString()
        String criteria = '{"name" : "' + venue1.name + '","latitude" : "' + venue1.location.latitude + '","longitude" : "' + venue1.location.longitude + '"}'
        mongoService.findOne(VENUE_COLLECTION, criteria) >> venue1Json
        venueJsonMarshaller.asVenue(venue1Json) >> venue1

        when:
        Venue fetchedVenue = mongoVenueRepository.findByNameAndCoordinates(venue1.name, venue1.location)

        then:
        fetchedVenue == venue1
    }

    def 'saves a venue using mongo service'() {
        given:
        Venue venue = createVenue()
        String venueJson = createVenueJsonString()
        venueJsonMarshaller.asVenueJsonString(_) >> venueJson
        mongoService.save(VENUE_COLLECTION, venueJson) >> venue.id

        when:
        def id = mongoVenueRepository.save(venue)


        then:
        venue.id == id

    }
}