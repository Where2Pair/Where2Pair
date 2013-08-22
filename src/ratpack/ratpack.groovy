import groovy.json.JsonBuilder

import static org.ratpackframework.groovy.RatpackScript.ratpack

def indexPages = ["index.html"] as String[]

ratpack {
    handlers {
        def venues = [[id: "2123123213", name: "a venue"], [id: '990-9090', name: 'another venue']]
        get("venues") {
            def json = new JsonBuilder(venues)
            response.send json.toString()
        }
        get("venue:venueId") {
            def venue = venues.findAll { it.id == pathTokens.venueId }
            def json = new JsonBuilder(venue)
            response.send json.toString()
        }

        assets "public", indexPages
    }
}