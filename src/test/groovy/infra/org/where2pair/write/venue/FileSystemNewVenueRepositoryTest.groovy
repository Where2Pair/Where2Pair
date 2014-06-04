package org.where2pair.write.venue

import static org.where2pair.write.venue.VenueJsonBuilder.venueJson

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileSystemNewVenueRepositoryTest extends Specification {

    @Rule TemporaryFolder tmp
    def timeProvider = Mock(CurrentTimeProvider)
    def venueJson = venueJson().build()
    def currentTime = 99L
    File rootFilePath
    FileSystemNewVenueRepository venueRepository

    def setup() {
        rootFilePath = tmp.newFolder()
        venueRepository = new FileSystemNewVenueRepository(rootFilePath, timeProvider)
        timeProvider.currentTimeMillis() >> currentTime
    }

    def 'saves venue json to disk'() {
        given:
        def newVenueSavedEvent = new NewVenueSavedEvent(new NewVenue(venueJson))
        def expectedPath = newVenueSavedEvent.venueId.toString() + File.separator + String.valueOf(currentTime)

        when:
        venueRepository.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        def expectedFile = new File(rootFilePath, expectedPath)
        expectedFile.text == venueJson.rawVenueJson
    }
}
