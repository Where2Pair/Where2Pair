package org.where2pair.main.venue

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.where2pair.core.venue.write.NewVenueSavedEvent
import org.where2pair.core.venue.write.NewVenueServiceFactory
import org.where2pair.infra.venue.persistence.HashMapVenueCache
import org.where2pair.infra.venue.persistence.VenueCachePopulator
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository
import org.where2pair.infra.venue.write.NewVenueController

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
