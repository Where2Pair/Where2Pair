package org.where2pair.write.venue

import static RawVenueJsonBuilder.rawVenueJson
import static org.where2pair.write.venue.RawVenueJsonBuilder.randomRawVenueJson

import org.junit.Rule
import org.junit.rules.TemporaryFolder
import spock.lang.Specification

class FileSystemNewVenueRepositoryTest extends Specification {

    @Rule TemporaryFolder tmp
    def timeProvider = Mock(CurrentTimeProvider)
    def rawVenueJson = rawVenueJson().build()
    def currentTime = new Random().nextLong()
    File rootFilePath
    FileSystemNewVenueRepository venueRepository

    def setup() {
        rootFilePath = tmp.newFolder()
        venueRepository = new FileSystemNewVenueRepository(rootFilePath, timeProvider)
    }

    def 'saves venue json to disk'() {
        given:
        timeProvider.currentTimeMillis() >> currentTime
        def newVenueSavedEvent = NewVenueSavedEvent.create(rawVenueJson)
        def expectedVenueDir = new File(rootFilePath, newVenueSavedEvent.venueId.toString())

        when:
        venueRepository.notifyNewVenueSaved(newVenueSavedEvent)

        then:
        def venueJsonFiles = expectedVenueDir.listFiles()
        venueJsonFiles.size() == 1
        with(venueJsonFiles[0]) {
            name.startsWith("$currentTime")
            text == rawVenueJson.payload
        }
    }

    def 'venue submissions with the same timestamps should be saved in separate files'() {
        given:
        timeProvider.currentTimeMillis() >> currentTime
        def (NewVenueSavedEvent event1, NewVenueSavedEvent event2) = twoIdenticalNewVenues()

        when:
        venueRepository.notifyNewVenueSaved(event1)
        venueRepository.notifyNewVenueSaved(event2)

        then:
        def venueSavedEvents = venueRepository.findAll()
        venueSavedEvents.size() == 2
        event1 in venueSavedEvents
        event2 in venueSavedEvents
    }

    def 'loads all venue json from disk, ordered descending by timestamp'() {
        given:
        def venueJson1 = randomRawVenueJson()
        def venueJson2 = randomRawVenueJson()
        def venueJson3 = randomRawVenueJson()
        def venueJson4 = randomRawVenueJson()
        createVenueJsonFile().withId('xyz').withTimestamp(101).withJson(venueJson1)
        createVenueJsonFile().withId('123').withTimestamp(102).withJson(venueJson2)
        createVenueJsonFile().withId('123').withTimestamp(103).withJson(venueJson3)
        createVenueJsonFile().withId('abc').withTimestamp(104).withJson(venueJson4)

        when:
        def venues = venueRepository.findAll()

        then:
        venues == [venueJson1, venueJson2, venueJson3, venueJson4].collect {
            NewVenueSavedEvent.create(it)
        }
    }

    private static List<NewVenueSavedEvent> twoIdenticalNewVenues() {
        def venueJson = rawVenueJson().build()
        [NewVenueSavedEvent.create(venueJson), NewVenueSavedEvent.create(venueJson)]
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

        void withJson(RawVenueJson json) {
            def jsonFile = new File(rootFilePath, venueId + File.separator + timestamp + '_' + UUID.randomUUID())
            jsonFile.parentFile.mkdirs()
            jsonFile.createNewFile()
            jsonFile.text = json.payload
        }
    }
}
