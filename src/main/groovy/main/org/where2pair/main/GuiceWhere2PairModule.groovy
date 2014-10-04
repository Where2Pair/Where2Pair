package org.where2pair.main

import static com.google.common.base.Preconditions.checkState
import static org.where2pair.write.venue.AsyncNewVenueSavedEventSubscriber.asAsyncSubscriber

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import org.where2pair.read.venue.HashMapVenueCache
import org.where2pair.read.venue.ShowVenueController
import org.where2pair.read.venue.TimeProvider
import org.where2pair.read.venue.find.FindVenueController
import org.where2pair.read.venue.find.VenueFinderService
import org.where2pair.read.venue.mappingtojson.VenueToJsonMapper
import org.where2pair.read.venue.opentimes.OpenTimesCriteriaFactory
import org.where2pair.write.venue.CurrentTimeProvider
import org.where2pair.write.venue.FileSystemNewVenueRepository
import org.where2pair.write.venue.NewVenueController
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueServiceFactory

class GuiceWhere2PairModule extends AbstractModule {

    static final PERSISTED_JSON_FILE_PATH = System.getenv('PERSISTED_JSON_FILE_PATH')

    @Provides
    @Singleton
    NewVenueController createNewVenueController(VenueCachePopulator venueCachePopulator,
                                                FileSystemNewVenueRepository venueRepository) {
        List<NewVenueSavedEvent> venues = venueRepository.findAll()
        venues.each { venueCachePopulator.notifyNewVenueSaved(it) }

        def newVenueService = NewVenueServiceFactory.createServiceWithEventSubscribers(
                asAsyncSubscriber(venueRepository),
                asAsyncSubscriber(venueCachePopulator))

        new NewVenueController(newVenueService)
    }

    @Provides
    FileSystemNewVenueRepository createFileSystemVenueRepository() {
        checkState(PERSISTED_JSON_FILE_PATH != null, 'PERSISTED_JSON_FILE_PATH env variable was not set')

        def rootFilePath = new File(PERSISTED_JSON_FILE_PATH)
        def timeProvider = new CurrentTimeProvider()
        new FileSystemNewVenueRepository(rootFilePath, timeProvider)
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
        def venueService = new VenueFinderService(venueCache)
        def openTimesCriteriaFactory = new OpenTimesCriteriaFactory(new TimeProvider())
        new FindVenueController(venueService, openTimesCriteriaFactory)
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

