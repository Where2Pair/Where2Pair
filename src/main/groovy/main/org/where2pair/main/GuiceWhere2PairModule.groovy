package org.where2pair.main

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.where2pair.write.venue.AmazonS3NewVenueRepository
import org.where2pair.write.venue.NewVenueController
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueServiceFactory

import static AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber

class GuiceWhere2PairModule extends AbstractModule {

    @Provides
    @Singleton
    NewVenueController createNewVenueController(VenueCachePopulator venueCachePopulator) {
        def venueRepository = new AmazonS3NewVenueRepository()

        List<NewVenueSavedEvent> venues = venueRepository.getAll()
        venues.each { venueCachePopulator.notifyNewVenueSaved(it) }

        new NewVenueServiceFactory().createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))
    }

    @Provides
    VenueCachePopulator createVenueCachePopulator(HashMapVenueCache venueCache) {
        new VenueCachePopulator(venueCache)
    }

    @Provides
    @Singleton
    HashMapVenueCache createVenueCache() {
        return new HashMapVenueCache()
    }

    @Override
    protected void configure() {
    }

}
