package org.where2pair.write.venue

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
        []
    }
}

