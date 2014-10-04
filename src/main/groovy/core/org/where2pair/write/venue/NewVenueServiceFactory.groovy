package org.where2pair.write.venue

class NewVenueServiceFactory {

    static NewVenueService createServiceWithEventSubscribers(
            NewVenueSavedEventSubscriber... newVenueSavedEventSubscribers) {
        def subscribers = new NewVenueSavedEventSubscribers(subscribers: newVenueSavedEventSubscribers)
        new NewVenueService(newVenueSavedEventSubscribers: subscribers)
    }

}

