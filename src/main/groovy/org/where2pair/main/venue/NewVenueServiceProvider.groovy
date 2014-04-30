package org.where2pair.main.venue

import com.google.inject.Provider
import groovyx.gpars.GParsExecutorsPool
import groovyx.gpars.GParsPool
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber
import org.where2pair.core.venue.write.NewVenueService
import org.where2pair.core.venue.write.NewVenueServiceFactory
import org.where2pair.infra.venue.persistence.VenueCachePopulator
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository

import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

import static NewVenueServiceProvider.AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber
import static java.util.concurrent.Executors.newCachedThreadPool

class NewVenueServiceProvider implements Provider<NewVenueService> {

    @Override
    NewVenueService get() {
        def venueRepository = new AmazonS3NewVenueRepository()
        def venueCachePopulator = new VenueCachePopulator()

        List venues = venueRepository.getAll()
        venues.each { venueCachePopulator.put(it) }

        def newVenueServiceFactory = new NewVenueServiceFactory()

        newVenueServiceFactory.createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))
    }

    private static class AsyncNewVenueSavedEventSubscriber implements NewVenueSavedEventSubscriber {
        private static final ExecutorService executorService = newCachedThreadPool()
        private final NewVenueSavedEventSubscriber subscriber

        static NewVenueSavedEventSubscriber asAsyncSubscriber(NewVenueSavedEventSubscriber newVenueSavedEventSubscriber) {
            new AsyncNewVenueSavedEventSubscriber(newVenueSavedEventSubscriber)
        }

        private AsyncNewVenueSavedEventSubscriber(NewVenueSavedEventSubscriber subscriber) {
            this.subscriber == subscriber
        }

        @Override
        void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
            GParsExecutorsPool.withExistingPool(executorService) {
                    Closure notify = {subscriber.notifyNewVenueSaved(newVenueSavedEvent)}
                    notify.callAsync()
            }
        }
    }
}
