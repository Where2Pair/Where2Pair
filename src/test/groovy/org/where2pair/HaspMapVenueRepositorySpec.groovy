package org.where2pair

import static org.where2pair.ObjectUtils.createVenue
import static org.where2pair.ObjectUtils.createDifferentVenue
import spock.lang.Specification

class HaspMapVenueRepositorySpec extends Specification {

	HashMapVenueRepository hashMapVenueRepository = new HashMapVenueRepository()
	
	def "returns an immutable copy of venues"() {
		given:
		10.times { hashMapVenueRepository.save(new Venue()) }
		
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
		hashMapVenueRepository.save(venue1)
		hashMapVenueRepository.save(venue2)
		
		when:
		Venue result = hashMapVenueRepository.get(venue2.id)
		
		then:
		result == venue2
	}
	
	def "returns fresh copies of venues"() {
		given:
		def venue = new Venue()
		hashMapVenueRepository.save(venue)
		
		when:
		def existingVenues = hashMapVenueRepository.getAll()
		def existingVenue = hashMapVenueRepository.get(venue.id)
		
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
	
	def "assigns and returns new id when saving venues"() {
		given:
		Venue venue1 = new Venue()
		Venue venue2 = new Venue()
		
		when:
		long venueId1 = hashMapVenueRepository.save(venue1)
		long venueId2 = hashMapVenueRepository.save(venue2)
		
		then:
		venueId1 == venue1.id
		venueId2 == venue2.id
		venue1.id != venue2.id
	}	
	
	def "finds venues by name and coordinates"() {
		given:
		Venue venue1 = createVenue()
		Venue venue2 = createDifferentVenue()
		hashMapVenueRepository.save(venue1)
		hashMapVenueRepository.save(venue2)
				
		when:
		Venue fetchedVenue = hashMapVenueRepository.findByNameAndCoordinates(venue2.name, venue2.location)
		
		then:
		fetchedVenue == venue2
	}
	
	def "updates venues"() {
		given:
		Venue originalVenue = createVenue()
		hashMapVenueRepository.save(originalVenue)
		Venue updatedVenue = createDifferentVenue()
		updatedVenue.id = originalVenue.id
		
		when:
		hashMapVenueRepository.update(updatedVenue)
		Venue storedVenue = hashMapVenueRepository.get(originalVenue.id)
	
		then:
		storedVenue == updatedVenue
	}
}
