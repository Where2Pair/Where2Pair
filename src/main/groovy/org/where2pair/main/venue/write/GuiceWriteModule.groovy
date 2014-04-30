package org.where2pair.main.venue.write

import com.google.inject.AbstractModule
import com.google.inject.Provides
import org.where2pair.core.venue.read.JsonToVenueDetailsMapper
import org.where2pair.core.venue.write.NewVenueServiceFactory
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository
import org.where2pair.infra.venue.write.NewVenueController
import org.where2pair.infra.venue.write.VenueCache
import org.where2pair.infra.venue.write.VenueCachePopulator
import org.where2pair.main.venue.persistence.VenueCacheProvider

class GuiceWriteModule extends AbstractModule {

    @Override
    protected void configure() {
        bind(VenueCache).toProvider(VenueCacheProvider)
    }

    @Provides
    NewVenueController createNewVenueController(VenueCache venueCache) {
        def venueRepository = new AmazonS3NewVenueRepository()
        def venueCachePopulator = createVenueCachePopulator(venueCache)

        List venues = venueRepository.getAll()
        venues.each { venueCachePopulator.put(it) }

        new NewVenueServiceFactory().createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))
    }

    private VenueCachePopulator createVenueCachePopulator(VenueCache venueCache) {
        new VenueCachePopulator(venueCache, new JsonToVenueDetailsMapper())
    }
}
