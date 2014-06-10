package org.where2pair.write.venue

import static RawVenueJsonBuilder.rawVenueJson

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
        timeProvider.currentTimeMillis() >> currentTime
    }

    def 'saves venue json to disk'() {
        given:
        def newVenueSavedEvent = new NewVenueSavedEvent(new NewVenue(VenueJson.parseFrom(rawVenueJson)))
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
        def (VenueJson venueJson1, VenueJson venueJson2) = twoIdenticalVenues()
        def newVenueSavedEvent1 = new NewVenueSavedEvent(new NewVenue(venueJson1))
        def newVenueSavedEvent2 = new NewVenueSavedEvent(new NewVenue(venueJson2))

        when:
        venueRepository.notifyNewVenueSaved(newVenueSavedEvent1)
        venueRepository.notifyNewVenueSaved(newVenueSavedEvent2)

        then:
        def venueSavedEvents = venueRepository.findAll()
        venueSavedEvents.size() == 2
        venueSavedEvents.find { it.venueJson.payload == venueJson1.payload }
        venueSavedEvents.find { it.venueJson.payload == venueJson2.payload }
    }

    def 'loads all venue json from disk, ordered descending by timestamp'() {
        given:
        createVenueJsonFile().withId('xyz').withTimestamp(101).withJson('{"json": 1}')
        createVenueJsonFile().withId('123').withTimestamp(102).withJson('{"json": 2}')
        createVenueJsonFile().withId('123').withTimestamp(103).withJson('{"json": 3}')
        createVenueJsonFile().withId('abc').withTimestamp(104).withJson('{"json": 4}')

        when:
        def venues = venueRepository.findAll()

        then:
        venues == ['{"json": 1}', '{"json": 2}', '{"json": 3}', '{"json": 4}'].collect {
            new NewVenueSavedEvent(new NewVenue(VenueJson.parseFrom(new RawVenueJson(it))))
        }
    }

    private static List<VenueJson> twoIdenticalVenues() {
        def rawVenueJsonBuilder = rawVenueJson()
        [VenueJson.parseFrom(rawVenueJsonBuilder.build()), VenueJson.parseFrom(rawVenueJsonBuilder.build())]
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
            def jsonFile = new File(rootFilePath, venueId + File.separator + timestamp + '_' + UUID.randomUUID())
            jsonFile.parentFile.mkdirs()
            jsonFile.createNewFile()
            jsonFile.text = json
        }
    }
}
