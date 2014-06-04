package org.where2pair.write.venue

import static groovy.io.FileType.FILES

import groovy.io.FileType
import groovy.transform.TupleConstructor

@TupleConstructor
class FileSystemNewVenueRepository implements NewVenueSavedEventSubscriber {

    final File rootFilePath
    final CurrentTimeProvider timeProvider

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        def venueDirectory = createVenueDirectory(newVenueSavedEvent)
        def venueJsonFile = createJsonFile(venueDirectory)
        venueJsonFile.text = newVenueSavedEvent.rawVenueJson
    }

    private File createVenueDirectory(NewVenueSavedEvent newVenueSavedEvent) {
        def venueDirectory = new File(rootFilePath, newVenueSavedEvent.venueId.toString())
        venueDirectory.mkdir()
        venueDirectory
    }

    private File createJsonFile(File venueDirectory) {
        def file = new File(venueDirectory, "${timeProvider.currentTimeMillis()}")
        file.createNewFile()
        file
    }

    List<NewVenueSavedEvent> getAll() {
        List<File> filePaths = []
        rootFilePath.eachFileRecurse(FILES) { filePaths << it }
        filePaths.sort { it.name }
        filePaths.collect { new NewVenueSavedEvent(new NewVenue(new VenueJson(it.text))) }
    }
}

