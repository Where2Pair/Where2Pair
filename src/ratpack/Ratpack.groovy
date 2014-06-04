import org.where2pair.common.venue.JsonResponse
import org.where2pair.main.GuiceWhere2PairModule
import org.where2pair.read.venue.find.FindVenueController
import org.where2pair.read.venue.ShowVenueController
import org.where2pair.write.venue.NewVenueController
import ratpack.http.Response
import ratpack.util.MultiValueMap

import static ratpack.groovy.Groovy.ratpack

ratpack {
    modules {
        register new GuiceWhere2PairModule()
    }

    handlers {
        get {
            response.send 'Welcome to Where2Pair! Your installation is working. For a list of the endpoints available, please see the documentation.'
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
    response.status(jsonResponse.statusCode.value, jsonResponse.responseBody)
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
