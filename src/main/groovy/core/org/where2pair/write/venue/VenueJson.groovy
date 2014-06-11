package org.where2pair.write.venue

import groovy.json.JsonSlurper
import groovy.transform.EqualsAndHashCode

@EqualsAndHashCode
class VenueJson {
    Map<String, ?> jsonMap
    RawVenueJson rawVenueJson

    VenueJson(RawVenueJson rawVenueJson) {
        this.jsonMap = parseJson(rawVenueJson)
        this.rawVenueJson = rawVenueJson
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
