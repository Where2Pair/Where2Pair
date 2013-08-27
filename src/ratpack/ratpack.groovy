import static org.ratpackframework.groovy.RatpackScript.ratpack
import static org.where2pair.DayOfWeek.MONDAY
import static org.where2pair.DayOfWeek.SUNDAY
import groovy.json.JsonBuilder

import org.where2pair.DistanceCalculator
import org.where2pair.TimeProvider;
import org.where2pair.VenueFinder
import org.where2pair.VenueRepository

import com.google.inject.AbstractModule

def indexPages = ["index.html"] as String[]
def venues = []

Set availableFeatures = ['wifi', 'mobile payments', 'baby changing']
Random random = new Random()
(MONDAY..SUNDAY).each { day ->
    (1d..10d).each {
        Set features = availableFeatures.findResults { if (random.nextBoolean()) return it }
        def venue = [
                id: random.nextLong().abs().toString(),
                name: "place-" + it,
                latitude: 1.0 + it/10,
                longitude: 0.1 + it/10,
                addressLine1: '123 Some Street',
                city: 'London',
                postcode: 'postcode',
                features: features,
                openPeriods: [day: day, openHour: 8, closeHour: 30]
        ]
        venues << venue
    }
}

class VenueFinderModule extends AbstractModule {
	protected void configure() {
		bind(TimeProvider)
		bind(DistanceCalculator)
		bind(VenueRepository).to(ArrayListVenueRepository).asSingleton()
		bind(VenueFinder)
		bind(VenueFinderController)
	}
}


ratpack {
	modules {
		register new VenueFinderModule()
	}
	
    handlers {
        prefix("venues") {
            get {
                def json = new JsonBuilder(venues)
                response.send json.toString()
            }
            get("nearest") { VenueFinderController venueFinderController ->
                response.send "nearest venues"
            }
        }
		prefix("venue") {
			get(":venueId") {
				def venue = venues.findAll { it.id == pathTokens.venueId }
				def json = new JsonBuilder(venue)
				response.send json.toString()
			}
			post {
				def json = new JsonBuilder(params)
				venue << json
				response.send json
			}
		}
        assets "public", indexPages
    }
}