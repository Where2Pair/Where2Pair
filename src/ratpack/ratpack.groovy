import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.where2pair.venue.DayOfWeek.MONDAY
import static org.where2pair.venue.DayOfWeek.SUNDAY
import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.where2pair.venue.ErrorResponse
import org.where2pair.venue.OpenHoursJsonMarshaller
import org.where2pair.venue.VenueJsonMarshaller
import org.where2pair.venue.VenueRepository
import org.where2pair.venue.find.DistanceCalculator
import org.where2pair.venue.find.TimeProvider
import org.where2pair.venue.find.VenueFinder
import org.where2pair.venue.find.VenueFinderController
import org.where2pair.venue.persist.HashMapVenueRepository
import org.where2pair.venue.save.SaveVenueController
import org.where2pair.venue.save.VenueSaveOrUpdater
import org.where2pair.venue.show.ShowVenueController

import com.google.inject.AbstractModule
import com.google.inject.Provides
import com.google.inject.Singleton

def indexPages = ["index.html"] as String[]
//def venues = []
//
//Set availableFeatures = ['wifi', 'mobile payments', 'baby changing']
//Random random = new Random()
//(MONDAY..SUNDAY).each { day ->
//    (1d..10d).each {
//        Set features = availableFeatures.findResults { if (random.nextBoolean()) return it }
//        def venue = [
//                id: random.nextLong().abs().toString(),
//                name: "place-" + it,
//                latitude: 1.0 + it/10,
//                longitude: 0.1 + it/10,
//                addressLine1: '123 Some Street',
//                city: 'London',
//                postcode: 'postcode',
//                features: features,
//                openPeriods: [day: day, openHour: 8, closeHour: 30]
//        ]
//        venues << venue
//    }
//}

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
    VenueFinderController createVenueFinderController(VenueRepository venueRepository, VenueJsonMarshaller venueJsonMarshaller){
        DistanceCalculator distanceCalculator = new DistanceCalculator()
        VenueFinder venueFinder = new VenueFinder(distanceCalculator: distanceCalculator, venueRepository: venueRepository)
        TimeProvider timeProvider = new TimeProvider()
        new VenueFinderController(timeProvider: timeProvider, venueFinder: venueFinder, venueJsonMarshaller: venueJsonMarshaller)
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
            get("nearest") { VenueFinderController venueFinderController ->
				def venues = venueFinderController.findNearest(request.queryParams)
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