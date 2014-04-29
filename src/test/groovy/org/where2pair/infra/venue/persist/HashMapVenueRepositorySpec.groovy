package org.where2pair.infra.venue.persist

import org.where2pair.core.venue.read.Venue
import org.where2pair.core.venue.common.VenueId
import org.where2pair.core.venue.VenueUtils
import org.where2pair.infra.venue.persistence.HashMapVenueCache
import spock.lang.Specification

import static org.where2pair.core.venue.ObjectUtils.createDifferentVenue
import static org.where2pair.core.venue.ObjectUtils.createVenue
import static org.where2pair.core.venue.read.VenueDetailsBuilder.venueDetails
import static org.where2pair.core.venue.VenueUtils.randomAddress
import static org.where2pair.core.venue.VenueUtils.randomName

class HashMapVenueRepositorySpec extends Specification {

    HashMapVenueCache hashMapVenueRepository = new HashMapVenueCache()

    def "returns an immutable copy of venues"() {
        given:
        10.times { hashMapVenueRepository.save(venueDetails().withName(randomName()).withAddress(randomAddress()).withLocation(VenueUtils.randomCoordinates()).build()) }

        when:
        def venues = hashMapVenueRepository.getAll()

        then:
        venues.size() == 10

        when:
        venues << new Venue()

        then:
        thrown(UnsupportedOperationException)
    }

    def "gets venues by id"() {
        given:
        Venue venue1 = new Venue()
        Venue venue2 = new Venue()
        String venue1Id = hashMapVenueRepository.save(venue1)
        String venue2Id = hashMapVenueRepository.save(venue2)

        when:
        Venue result = hashMapVenueRepository.get(venue2Id)

        then:
        result == venue2

        when:
        result = hashMapVenueRepository.get(venue1Id)

        then:
        result == venue1
    }

    def "returns fresh copies of venues"() {
        given:
        def venue = new Venue()
        String venueId = hashMapVenueRepository.save(venue)

        when:
        def existingVenues = hashMapVenueRepository.getAll()
        def existingVenue = hashMapVenueRepository.get(venueId)

        then:
        !existingVenues[0].is(venue)
        !existingVenue.is(venue)
        existingVenues[0] == venue
        existingVenue == venue
    }

    def "saves copy of venue"() {
        given:
        Venue originalVenue = createVenue()
        hashMapVenueRepository.save(originalVenue)

        when:
        originalVenue.name = 'a different name'
        Venue fetchedVenue = hashMapVenueRepository.get(originalVenue.id)

        then:
        fetchedVenue != originalVenue
    }

    def "returns id when saving venues"() {
        given:
        Venue venue1 = createVenue()
        Venue venue2 = createDifferentVenue()

        when:
        VenueId venueId1 = hashMapVenueRepository.save(venue1)
        VenueId venueId2 = hashMapVenueRepository.save(venue2)

        then:
        venue1Id == new VenueId(venue1)
        venue2Id == new VenueId(venue2)
    }

    def "returns null when venues don't exist"() {
        when:
        Venue fetchedVenue = hashMapVenueRepository.get('99')

        then:
        fetchedVenue == null
    }

//    def "updates venues"() {
//        given:
//        Venue originalVenue = createVenue()
//        hashMapVenueRepository.save(originalVenue)
//        Venue updatedVenue = createDifferentVenue()
//        updatedVenue.id = originalVenue.id
//
//        when:
//        hashMapVenueRepository.update(updatedVenue)
//        Venue storedVenue = hashMapVenueRepository.get(originalVenue.id)
//
//        then:
//        storedVenue == updatedVenue
//    }
}
