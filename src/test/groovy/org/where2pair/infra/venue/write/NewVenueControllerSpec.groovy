package org.where2pair.infra.venue.write

import org.where2pair.core.venue.write.NewVenueService
import spock.lang.Specification

import static org.where2pair.core.venue.VenueIdBuilder.aVenueId
import static org.where2pair.core.venue.read.VenueBuilder.aVenue

class NewVenueControllerSpec extends Specification {

    NewVenueService newVenueService = Mock()
    NewVenueController controller = new NewVenueController(newVenueService: newVenueService)

    def 'saves new venues and returns venue id'() {
        given:
        def venueJson = aVenue().toJson()
        def expectedVenueId = aVenueId().build()
        newVenueService.save(venueJson) >> expectedVenueId

        when:
        def venueIdAsString = controller.save(venueJson)

        then:
        venueIdAsString == expectedVenueId.toString()
    }

}
