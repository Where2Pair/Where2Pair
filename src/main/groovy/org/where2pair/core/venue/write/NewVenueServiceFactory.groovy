package org.where2pair.core.venue.write


class NewVenueServiceFactory {

    NewVenueService createServiceWithEventSubscribers(
            NewVenueSavedEventSubscriber... newVenueSavedEventSubscribers) {
        def subscribers = new NewVenueSavedEventSubscribers(subscribers: newVenueSavedEventSubscribers)
        new NewVenueService(newVenueSavedEventSubscribers: subscribers)
    }

}
