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

    def 'loads all venue json from disk, ordered descending by timestamp'() {
        given:
        createVenueJsonFile().withId('xyz').withTimestamp(101).withJson('json: 1')
        createVenueJsonFile().withId('123').withTimestamp(102).withJson('json: 2')
        createVenueJsonFile().withId('123').withTimestamp(103).withJson('json: 3')
        createVenueJsonFile().withId('abc').withTimestamp(104).withJson('json: 4')

        when:
        def venues = venueRepository.findAll()

        then:
        venues == ['json: 1', 'json: 2', 'json: 3', 'json: 4'].collect {
            new NewVenueSavedEvent(new NewVenue(new VenueJson(it)))
        }
    }

    private VenueJsonFileBuilder createVenueJsonFile() {
        new VenueJsonFileBuilder()
    }

    class VenueJsonFileBuilder {
        String venueId
        int timestamp

        VenueJsonFileBuilder withId(String id) {
            this.venueId = id
            this
        }

        VenueJsonFileBuilder withTimestamp(int timestamp) {
            this.timestamp = timestamp
            this
        }

        void withJson(String json) {
            def jsonFile = new File(rootFilePath, venueId + File.separator + timestamp)
            jsonFile.parentFile.mkdirs()
            jsonFile.createNewFile()
            jsonFile.text = json
        }
    }
}
