package org.where2pair.write.venue

import groovy.json.JsonSlurper
import groovy.transform.Immutable

@Immutable
class VenueJson {
    Map<String, ?> jsonMap
    RawVenueJson rawVenueJson

    static VenueJson parseFrom(RawVenueJson rawVenueJson) {
        def jsonMap = parseJson(rawVenueJson)
        new VenueJson(jsonMap, rawVenueJson)
    }

    private static Map<String, ?> parseJson(RawVenueJson rawVenueJson) {
        def json = new JsonSlurper().parseText(rawVenueJson.payload)
        if (!(json instanceof Map))
            throw new InvalidVenueJsonException('Venue json not in the expected format')
        json as Map<String, ?>
    }

    def propertyMissing(String property) {
        jsonMap[property]
    }

    String getPayload() {
        rawVenueJson.payload
    }
}
