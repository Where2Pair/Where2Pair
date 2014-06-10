package org.where2pair.write.venue

class NewVenueService {

    NewVenueSavedEventSubscribers newVenueSavedEventSubscribers

    NewVenueId save(RawVenueJson rawVenueJson) {
        def venueJson = VenueJson.parseFrom(rawVenueJson)
        NewVenueSavedEvent newVenueSavedEvent = NewVenue.publishNewVenue(venueJson)
        newVenueSavedEventSubscribers.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}

