import groovy.json.JsonBuilder
import groovy.json.JsonSlurper

import org.where2pair.infra.venue.read.FindVenueController
import org.where2pair.infra.venue.read.ShowVenueController
import org.where2pair.infra.venue.write.NewVenueController
import org.where2pair.main.venue.GuiceWhere2PairModule

import static ratpack.groovy.Groovy.ratpack

ratpack {
    modules {
        register new GuiceWhere2PairModule()
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