package org.where2pair.grails

import grails.converters.JSON
import org.where2pair.Venue

class VenueController {

    GormVenueRepository gormVenueRepository
    VenueConverter venueConverter

    def show(long id) {
        Venue venue = gormVenueRepository.get(id)
        VenueDTO venueDto = venueConverter.asVenueDto(venue)
        render new JSON(venueDto)
    }

    def showAll() {
        List venues = gormVenueRepository.getAll()
        List venueDTOs = asVenueDTOs(venues)
        render venueDTOs as JSON
    }

    def save() {
        VenueDTO venueDTO = new VenueDTO(request.JSON)
        long id = gormVenueRepository.save(venueDTO)
        venueDTO.id = id
        render new JSON(venueDTO)
    }

    private List asVenueDTOs(List venues) {
        venueConverter.asVenueDtos(venues)
    }

}
