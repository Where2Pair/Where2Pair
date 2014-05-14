package org.where2pair.write.venue


class NewVenueServiceFactory {

    NewVenueService createServiceWithEventSubscribers(
            NewVenueSavedEventSubscriber... newVenueSavedEventSubscribers) {
        def subscribers = new NewVenueSavedEventSubscribers(subscribers: newVenueSavedEventSubscribers)
        new NewVenueService(newVenueSavedEventSubscribers: subscribers)
    }

}
