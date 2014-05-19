package org.where2pair.main

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.where2pair.read.venue.FindVenueController
import org.where2pair.read.venue.LocationsCriteriaParser
import org.where2pair.read.venue.ShowVenueController
import org.where2pair.read.venue.TimeProvider
import org.where2pair.read.venue.VenueRepository
import org.where2pair.read.venue.VenueService
import org.where2pair.read.venue.find.OpenTimesCriteriaFactory
import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper
import org.where2pair.write.venue.AmazonS3NewVenueRepository
import org.where2pair.write.venue.NewVenueController
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueServiceFactory

import static AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber

class GuiceWhere2PairModule extends AbstractModule {

    @Provides
    @Singleton
    NewVenueController createNewVenueController(VenueCachePopulator venueCachePopulator) {
        println 'creating controller'
        def venueRepository = new AmazonS3NewVenueRepository()

        List<NewVenueSavedEvent> venues = venueRepository.getAll()
        venues.each { venueCachePopulator.notifyNewVenueSaved(it) }

        def newVenueService = new NewVenueServiceFactory().createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))

        println 'returning'

        new NewVenueController(newVenueService)
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

    @Provides
    @Singleton
    FindVenueController createFindVenueController(HashMapVenueCache venueCache) {
        def venueService = new VenueService(venueCache)
        def locationsCriteriaParser = new LocationsCriteriaParser()
        def openTimesCriteriaFactory = new OpenTimesCriteriaFactory(new TimeProvider())
        new FindVenueController(venueService, locationsCriteriaParser, openTimesCriteriaFactory)
    }

    @Provides
    @Singleton
    ShowVenueController createShowVenueController(HashMapVenueCache venueCache) {
        new ShowVenueController(venueCache, new VenueToJsonMapper())
    }

    @Override
    protected void configure() {
    }

}
