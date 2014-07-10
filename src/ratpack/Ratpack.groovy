import static ratpack.groovy.Groovy.ratpack

import org.where2pair.common.venue.JsonResponse
import org.where2pair.main.GuiceWhere2PairModule
import org.where2pair.read.venue.ShowVenueController
import org.where2pair.read.venue.find.FindVenueController
import org.where2pair.write.venue.NewVenueController
import ratpack.codahale.metrics.CodaHaleMetricsModule
import ratpack.http.Response
import ratpack.util.MultiValueMap

ratpack {
    bindings {
        add new CodaHaleMetricsModule().jmx()
        add new GuiceWhere2PairModule()
    }

    handlers {
        get {
            response.contentType 'text/html'
            response.send landingPage
        }
        prefix('venues') {
            get('nearest') { FindVenueController findVenueController ->
                def queryParams = squashLocationQueryParamValuesIntoList(request.queryParams)
                def venues = findVenueController.findNearest(queryParams)
                renderResult(response, venues)
            }
        }
        prefix('venue') {
            get(':venueId') { ShowVenueController showVenueController ->
                def venue = showVenueController.show(pathTokens.venueId)
                renderResult(response, venue)
            }
            post { NewVenueController newVenueController ->
                def venueId = newVenueController.save(request.body.text)
                renderResult(response, venueId)
            }
        }
    }
}

def renderResult(Response response, JsonResponse jsonResponse) {
    response.status(jsonResponse.statusCode.value)
    response.contentType('application/json')
    response.send(jsonResponse.responseBody)
}

def squashLocationQueryParamValuesIntoList(MultiValueMap<String, String> queryParams) {
    queryParams.collectEntries { key, value ->
        if (key == 'location') {
            return [(key): queryParams.getAll(key)]
        }
        [(key): value]
    }
}

String getLandingPage() {
    """
    |<h2>Welcome to Where2Pair!</h2>
    |
    |<h4>Find venues sample request (GET):</h4>
    |
    |<i>/venues/nearest?location=1.0,0.1&openDay=monday&openFrom=12.30&openUntil=18.30&withFacilities=wifi</i>
    |
    |<h4>Show venue sample request (GET):</h4>
    |
    |<i>/venue/<b>:venueId</b></i>
    |
    |<h4>Upload venue sample request (POST):</h4>
    |
    |<i>/venue</i>
    |
    |<p>Sample venue json request body:</p>
    |
    |<i>{
    |    "name": "venue name",
    |    "address": {
    |        "addressLine1": "addressLine1",
    |        "addressLine2": "addressLine2",
    |        "addressLine3": "addressLine3",
    |        "city": "city",
    |        "postcode": "postcode",
    |        "phoneNumber": "01234567890"
    |    },
    |    "location": {
    |        "latitude": 1.0,
    |        "longitude": 0.1
    |    },
    |    "openHours": {
    |        "monday": [
    |            {
    |                "openHour": 12,
    |                "openMinute": 0,
    |                "closeHour": 18,
    |                "closeMinute": 30
    |            }
    |        ]
    |    },
    |    "facilities": {
    |        "wifi": "Y",
    |        "mobile payments": "N",
    |        "power": "UNKNOWN"
    |    }
    |}</i>
    |""".stripMargin()
}
