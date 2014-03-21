package org.where2pair.core.venue


interface VenueRepository {

    List getAll()

    Venue get(String id)

    String save(Venue venue)

    Venue findByNameAndCoordinates(String name, Coordinates coordinates)

    void update(Venue venue)
}
