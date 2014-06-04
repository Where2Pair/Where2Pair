package org.where2pair.main

import groovy.json.JsonSlurper
import groovy.transform.TupleConstructor
import org.where2pair.read.venue.Venue
import org.where2pair.read.venue.VenueId
import org.where2pair.read.venue.mappingfromjson.JsonToVenueDetailsMapper
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueSavedEventSubscriber

@TupleConstructor
class VenueCachePopulator implements NewVenueSavedEventSubscriber {

    final HashMapVenueCache venueCache
    final JsonToVenueDetailsMapper jsonToVenueDetailsMapper = new JsonToVenueDetailsMapper()

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        Map<String, ?> venueJsonMap = parseVenueJson(newVenueSavedEvent.rawVenueJson)
        def venueDetails = jsonToVenueDetailsMapper.toVenueDetails(venueJsonMap)
        def venueId = new VenueId(newVenueSavedEvent.venueId.toString())
        def venue = Venue.newInstance(venueId, venueDetails)
        venueCache.put(venue)
    }

    private static Map<String, ?> parseVenueJson(String rawVenueJson) {
        new JsonSlurper().parseText(rawVenueJson)
    }
}

