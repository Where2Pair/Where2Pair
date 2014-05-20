package org.where2pair.write.venue

class NewVenueService {

    NewVenueSavedEventSubscribers newVenueSavedEventSubscribers

    NewVenueId save(Map<String, ?> venueJson) {
        NewVenueSavedEvent newVenueSavedEvent = NewVenue.publishNewVenue(venueJson)
        newVenueSavedEventSubscribers.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}
