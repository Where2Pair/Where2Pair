package org.where2pair.main.venue

import com.google.inject.Inject
import com.google.inject.Provider
import groovyx.gpars.GParsExecutorsPool
import org.where2pair.core.venue.read.JsonToVenueDetailsMapper
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueSavedEventSubscriber
import org.where2pair.core.venue.write.NewVenueServiceFactory
import org.where2pair.infra.venue.write.NewVenueController
import org.where2pair.infra.venue.write.VenueCache
import org.where2pair.infra.venue.write.VenueCachePopulator
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository

import java.util.concurrent.ExecutorService

import static NewVenueControllerProvider.AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber
import static java.util.concurrent.Executors.newCachedThreadPool

class NewVenueControllerProvider implements Provider<NewVenueController> {

    @Inject
    final VenueCache venueCache

    @Override
    NewVenueController get() {
        def venueRepository = new AmazonS3NewVenueRepository()
        def venueCachePopulator = createVenueCachePopulator()

        List venues = venueRepository.getAll()
        venues.each { venueCachePopulator.put(it) }

        new NewVenueServiceFactory().createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))
    }

    private VenueCachePopulator createVenueCachePopulator() {
        new VenueCachePopulator(venueCache, new JsonToVenueDetailsMapper())
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
