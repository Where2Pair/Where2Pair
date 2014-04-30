import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.where2pair.infra.venue.write.AmazonS3NewVenueRepository
import org.where2pair.infra.venue.write.VenueCachePopulator
import org.where2pair.infra.venue.web.LocationsCriteriaParser
import org.where2pair.core.venue.read.TimeProvider
import org.where2pair.core.venue.read.VenueService
import org.where2pair.infra.venue.web.ErrorResponse
import org.where2pair.infra.venue.web.FindVenueController

import org.where2pair.infra.venue.web.VenueJsonMarshaller
import org.where2pair.core.venue.read.VenueRepository
import org.where2pair.infra.venue.persistence.HashMapVenueCache
import org.where2pair.infra.venue.write.NewVenueController

import org.where2pair.infra.venue.web.ShowVenueController

import static ratpack.groovy.Groovy.ratpack

//TODO how can we split these modules? How can we make common resources available to both modules (i.e. the HashMapVenueCache)?
//Or do we later say NewVenueSavedEventPublisher.setNewEventSavedListener(...)?
class Where2PairModule extends AbstractModule {

//    @Provides
//    ShowVenueController createShowVenueController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller) {
//        new ShowVenueController(venueRepository: venueRepository, venueJsonMarshaller: venueJsonMarshaller)
//    }
//
//    @Provides
//    NewVenueController createSaveVenueController(NewVenueRepository newVenueRepository, NewVenueSavedEventPublisher newVenueSavedEventPublisher) {
//        println 'creating save venue controller'
//        NewVenueService newVenueService = new NewVenueService(newVenueRepository: newVenueRepository, newVenueSavedEventSubscribers: newVenueSavedEventPublisher)
//        new NewVenueController(newVenueService: newVenueService)
//    }
//
//    @Provides
//    NewVenueSavedEventPublisher createNewVenueSavedEventPublisher(VenueCachePopulator asyncVenueCachePopulator) {
//        println 'creating new venue saved event publisher'
//        println 'creating...'
//        try {
//            NewVenueSavedEventPublisher pub = new NewVenueSavedEventPublisher(asyncVenueCachePopulator)
//            println 'created'
//            return pub
//        }
//        catch (Exception e) {
//            println e.message
//        }
//    }

    @Provides
    @Singleton
    VenueCachePopulator createAsyncVenueCachePopulator(HashMapVenueCache hashMapVenueCache, VenueJsonMarshaller venueJsonMarshaller) {
        println 'creating cache populator'
        new VenueCachePopulator(hashMapVenueCache, venueJsonMarshaller)
    }

    @Provides
    FindVenueController createFindVenueController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller) {
        VenueService venueFinder = new VenueService(venueRepository: venueRepository)
        TimeProvider timeProvider = new TimeProvider()
        LocationsCriteriaParser locationsCriteriaParser = new LocationsCriteriaParser()
        new FindVenueController(timeProvider: timeProvider, locationsCriteriaParser: locationsCriteriaParser, venueFinder: venueFinder, venueJsonMarshaller: venueJsonMarshaller)
    }

    @Override
    protected void configure() {
        bind(NewVenueRepository).to(AmazonS3NewVenueRepository).in(Singleton)
        bind(VenueRepository).to(HashMapVenueCache).in(Singleton)
    }
}

ratpack {
    modules {
        register new Where2PairModule()
    }

    handlers {
        get {
            response.send "Welcome to Where2Pair!!! Your installation is working. For a list of the endpoints available, please see the documentation."
        }
        prefix("venues") {
            get { ShowVenueController showVenueController ->
                def venues = showVenueController.showAll()
                renderResult(response, venues)
            }
            get("nearest") { FindVenueController findVenueController ->
				def queryParams = squashLocationQueryParamValuesIntoList(request.queryParams)
                def venues = findVenueController.findNearest(queryParams)
                renderResult(response, venues)
            }
        }
        prefix("venue") {
            get(":venueId") { ShowVenueController showVenueController ->
                def venue = showVenueController.show(Long.parseLong(pathTokens.venueId))
                renderResult(response, venue)
            }
            post { NewVenueController saveVenueController ->
                println 'savingVenueController'
                def json = new JsonSlurper().parseText(request.body.text)
                def venue = saveVenueController.save(json)
                renderResult(response, venue)
            }
        }
    }
}

def renderResult(response, ErrorResponse errorResponse) {
    response.status(errorResponse.status, errorResponse.message)
    response.send(errorResponse.message)
}

def renderResult(response, result) {
    String json = new JsonBuilder(result).toString()
    response.send("application/json", json)
}

def squashLocationQueryParamValuesIntoList(queryParams) {
	queryParams.collectEntries { key, value ->
		if (key == 'location') {
			return [(key): queryParams.getAll(key)]
		}
		[(key): value]
	}
}