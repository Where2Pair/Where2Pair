package org.where2pair.grails

import grails.converters.JSON
import org.where2pair.Venue

class VenueController {

    GormVenueRepository gormVenueRepository
    VenueConverter venueConverter

    def show(long id) {
        Venue venue = gormVenueRepository.get(id)
        VenueDto venueDto = venueConverter.asVenueDto(venue)
        render new JSON(venueDto)
    }

    def showAll() {
        List venues = gormVenueRepository.getAll()
        List venueDtos = asVenueDtos(venues)
        render venueDtos as JSON
    }

    def save() {
        VenueDto venueDto = new VenueDto(request.JSON)
        long id = gormVenueRepository.save(venueDto)
        venueDto.id = id
        render new JSON(venueDto)
    }

    private List asVenueDtos(List venues) {
        venueConverter.asVenueDtos(venues)
    }

}
