package org.where2pair.write.venue

import groovy.json.JsonSlurper
import groovy.transform.EqualsAndHashCode
import groovy.transform.TupleConstructor

@EqualsAndHashCode
@TupleConstructor
class VenueJson {
    final String rawVenueJson
    final static Closure<Map<String, ?>> CACHING_JSON_MAP_PARSER = { rawVenueJson ->
        def json = new JsonSlurper().parseText(rawVenueJson)
        if (!(json instanceof Map))
            throw new InvalidVenueJsonException('Venue json not in the expected format')
        json as Map<String, ?>
    }.memoize()

    def propertyMissing(String property) {
        def jsonMap = CACHING_JSON_MAP_PARSER.call(rawVenueJson)
        jsonMap[property]
    }

    Map<String, ?> getJsonMap() {
        CACHING_JSON_MAP_PARSER.call(rawVenueJson)
    }
}
