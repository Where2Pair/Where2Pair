import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper
import org.where2pair.infra.venue.web.LocationsCriteriaParser
import org.where2pair.core.venue.TimeProvider
import org.where2pair.core.venue.VenueService
import org.where2pair.infra.venue.web.ErrorResponse
import org.where2pair.infra.venue.web.FindVenueController
import org.where2pair.infra.venue.web.OpenHoursJsonMarshaller
import org.where2pair.infra.venue.web.VenueJsonMarshaller
import org.where2pair.core.venue.VenueRepository
import org.where2pair.infra.venue.persistence.HashMapVenueRepository
import org.where2pair.infra.venue.web.SaveVenueController

import org.where2pair.infra.venue.web.ShowVenueController

import static ratpack.groovy.Groovy.ratpack

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
        VenueService venueService = new VenueService(venueRepository: venueRepository)
        new SaveVenueController(venueService: venueService, venueJsonMarshaller: venueJsonMarshaller)
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
        bind(VenueRepository).to(HashMapVenueRepository).in(Singleton)
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
            post { SaveVenueController saveVenueController ->
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