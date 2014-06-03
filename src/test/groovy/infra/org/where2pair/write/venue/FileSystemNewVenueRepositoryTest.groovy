package org.where2pair.write.venue

import static org.where2pair.write.venue.VenueJsonBuilder.venueJson

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileSystemNewVenueRepositoryTest extends Specification {

    @Rule TemporaryFolder tmp
    def timeProvider = Mock(CurrentTimeProvider)

    def 'saves venue json to disk'() {
        given:
        def venueJson = venueJson().build()
        def newVenue = new NewVenue(venueJson)
        def newVenueSavedEvent = new NewVenueSavedEvent(newVenue)
        def rootFilePath = tmp.newFolder()
        def venueRepository = new FileSystemNewVenueRepository(rootFilePath, timeProvider)
        def currentTime = 99L
        timeProvider.currentTimeMillis() >> currentTime

        when:
        venueRepository.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        def expectedFile = new File(rootFilePath, newVenue.venueId.encode() + File.separator + String.valueOf(currentTime))
        expectedFile.text == venueJson.rawVenueJson
    }
}
