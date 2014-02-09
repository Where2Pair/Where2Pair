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
        String venue = mongoService.findById(VENUE_COLLECTION, "$id")
        venueJsonMarshaller.asVenue(venue)
    }

    @Override
    long save(Venue venue) {
        Long.parseLong(mongoService.save(VENUE_COLLECTION, venueJsonMarshaller.asVenueJsonString(venue)))
    }

    @Override
    Venue findByNameAndCoordinates(String name, Coordinates coordinates) {
        String criteria = '{"name" : "' + name + '","latitude" : "' + coordinates.latitude + '","longitude" : "' + coordinates.longitude + '"}'
        String venueJson = mongoService.findOne(VENUE_COLLECTION, criteria)
        venueJsonMarshaller.asVenue(venueJson)
    }

    @Override
    void update(Venue venue) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
