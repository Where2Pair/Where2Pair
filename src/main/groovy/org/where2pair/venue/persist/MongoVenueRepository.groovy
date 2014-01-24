package org.where2pair.venue.persist

import org.where2pair.venue.Coordinates
import org.where2pair.venue.Venue
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.VenueRepository

class MongoVenueRepository implements VenueRepository {
    public static final String VENUE_COLLECTION = "venue-collection"

    MongoService mongoService
    VenueJsonMarshaller venueJsonMarshaller

    @Override
    List getAll() {
        String venuesJson = mongoService.find(VENUE_COLLECTION)
        venueJsonMarshaller.asVenues(venuesJson)
    }

    @Override
    Venue get(long id) {
        String criteria = '{"id" : "' + id + '"}'
        String venue = mongoService.findOne(VENUE_COLLECTION, criteria)
        venueJsonMarshaller.asVenue(venue)
    }

    @Override
    long save(Venue venue) {
        return 0  //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    Venue findByNameAndCoordinates(String name, Coordinates coordinates) {
        String criteria = '{"name" : "' + name + '","lat" : "' + coordinates.lat + '","lng" : "' + coordinates.lng + '"}'
        String venueJson = mongoService.findOne(VENUE_COLLECTION, criteria)
        venueJsonMarshaller.asVenue(venueJson)
    }

    @Override
    void update(Venue venue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
