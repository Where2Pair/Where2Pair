package org.where2pair.write.venue

import static groovy.io.FileType.FILES
import static java.util.UUID.randomUUID

import groovy.transform.TupleConstructor

@TupleConstructor
class FileSystemNewVenueRepository implements NewVenueSavedEventSubscriber {

    static final String JOIN_CHAR = '_'

    final File rootFilePath
    final CurrentTimeProvider timeProvider

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        def venueDirectory = createVenueDirectory(newVenueSavedEvent)
        def venueJsonFile = createJsonFile(venueDirectory)
        venueJsonFile.text = newVenueSavedEvent.venueJsonPayload
    }

    private File createVenueDirectory(NewVenueSavedEvent newVenueSavedEvent) {
        def venueDirectory = new File(rootFilePath, newVenueSavedEvent.venueId.toString())
        venueDirectory.mkdir()
        venueDirectory
    }

    private File createJsonFile(File venueDirectory) {
        def uniqueFilename = "${timeProvider.currentTimeMillis()}" + JOIN_CHAR + randomUUID()
        def file = new File(venueDirectory, uniqueFilename)
        file.createNewFile()
        file
    }

    List<NewVenueSavedEvent> findAll() {
        List<File> filePaths = []
        rootFilePath.eachFileRecurse(FILES) { filePaths << it }
        filePaths.sort { timestampFromFilename(it) }
        filePaths.collect { file -> newVenueSavedEvent(file.text) }
    }

    private static String timestampFromFilename(File file) {
        file.name.split(JOIN_CHAR)[0]
    }

    private static NewVenueSavedEvent newVenueSavedEvent(String json) {
        new NewVenueSavedEvent(new NewVenue(VenueJson.parseFrom(new RawVenueJson(json))))
    }
}

