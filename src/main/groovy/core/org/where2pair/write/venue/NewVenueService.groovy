package org.where2pair.write.venue

class NewVenueService {
    NewVenueSavedEventSubscribers newVenueSavedEventSubscribers

    NewVenueId save(RawVenueJson rawVenueJson) {
        def newVenueSavedEvent = NewVenueSavedEvent.create(rawVenueJson)
        newVenueSavedEventSubscribers.publish(newVenueSavedEvent)
        newVenueSavedEvent.venueId
    }

}

