import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.where2pair.venue.DayOfWeek.MONDAY
import static org.where2pair.venue.DayOfWeek.SUNDAY
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.where2pair.venue.OpenHoursJsonMarshaller
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.VenueRepository
import org.where2pair.venue.find.ErrorResponse;
import org.where2pair.venue.find.LocationsCriteriaParser
import org.where2pair.venue.find.TimeProvider
import org.where2pair.venue.find.VenueFinder
import org.where2pair.venue.find.FindVenueController
import org.where2pair.venue.persist.HashMapVenueRepository
import org.where2pair.venue.save.SaveVenueController
import org.where2pair.venue.save.VenueSaveOrUpdater
import org.where2pair.venue.show.ShowVenueController

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

def indexPages = ["index.html"] as String[]

class Where2PairModule extends AbstractModule {
    @Provides
    VenueJsonMarshaller createVenueJsonMarshaller() {
        OpenHoursJsonMarshaller openHoursJsonMarshaller = new OpenHoursJsonMarshaller()
        new VenueJsonMarshaller(openHoursJsonMarshaller: openHoursJsonMarshaller)
    }

    @Provides
    ShowVenueController createShowVenueController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller) {
        new ShowVenueController(venueRepository: venueRepository, venueJsonMarshaller: venueJsonMarshaller)
    }

    @Provides
    SaveVenueController createSaveVenueController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller) {
        VenueSaveOrUpdater venueSaveOrUpdater = new VenueSaveOrUpdater(venueRepository: venueRepository)
        new SaveVenueController(venueSaveOrUpdater: venueSaveOrUpdater, venueJsonMarshaller: venueJsonMarshaller)
    }

    @Provides
    FindVenueController createFindVenueController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller) {
        VenueFinder venueFinder = new VenueFinder(venueRepository: venueRepository)
        TimeProvider timeProvider = new TimeProvider()
        LocationsCriteriaParser locationsCriteriaParser = new LocationsCriteriaParser()
        new FindVenueController(timeProvider: timeProvider, locationsCriteriaParser: locationsCriteriaParser, venueFinder: venueFinder, venueJsonMarshaller: venueJsonMarshaller)
    }

    @Override
    protected void configure() {
        bind(VenueRepository).to(HashMapVenueRepository).in(Singleton)
    }
}

ratpack {
    modules {
        register new Where2PairModule()
    }

    handlers {
        prefix("venues") {
            get { ShowVenueController showVenueController ->
                def venues = showVenueController.showAll()
                renderResult(response, venues)
            }
            get("nearest") { FindVenueController findVenueController ->
                def venues = findVenueController.findNearest(request.queryParams)
                renderResult(response, venues)
            }
        }
        prefix("venue") {
            get(":venueId") { ShowVenueController showVenueController ->
                def venue = showVenueController.show(Long.parseLong(pathTokens.venueId))
                renderResult(response, venue)
            }
            post { SaveVenueController saveVenueController ->
                def json = new JsonSlurper().parseText(request.text)
                def venue = saveVenueController.save(json)
                renderResult(response, venue)
            }
        }
        assets "public", indexPages
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