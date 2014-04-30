package org.where2pair.main.venue

import com.google.inject.Provider
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber
import org.where2pair.core.venue.write.NewVenueService
import org.where2pair.core.venue.write.NewVenueServiceFactory
import org.where2pair.infra.venue.persistence.VenueCachePopulator
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository

import static NewVenueServiceProvider.AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber

class NewVenueServiceProvider implements Provider<NewVenueService> {

    @Override
    NewVenueService get() {
        def venueRepository = new AmazonS3NewVenueRepository()
        def venueCache = new VenueCachePopulator()

        List venues = venueRepository.getAll()
        venues.each { venueCache.put(it) }

        def newVenueServiceFactory = new NewVenueServiceFactory()

        newVenueServiceFactory.createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))
    }

    private static class AsyncNewVenueSavedEventSubscriber implements NewVenueSavedEventSubscriber {
        private final NewVenueSavedEventSubscriber subscriber

        static NewVenueSavedEventSubscriber asAsyncSubscriber(NewVenueSavedEventSubscriber newVenueSavedEventSubscriber) {
            new AsyncNewVenueSavedEventSubscriber(newVenueSavedEventSubscriber)
        }

        private AsyncNewVenueSavedEventSubscriber(NewVenueSavedEventSubscriber subscriber) {
            this.subscriber == subscriber
        }

        @Override
        void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
            subscriber.notifyNewVenueSaved(newVenueSavedEvent)
        }
    }
}
