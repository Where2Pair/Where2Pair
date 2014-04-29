package org.where2pair.core.venue.write


class NewVenueSavedEventSubscribers {

    private List<NewVenueSavedEventSubscriber> subscribers

    void publish(NewVenueSavedEvent newVenueSavedEvent) {
        subscribers.each {
            it.notifyNewVenueSaved(newVenueSavedEvent)
        }
    }
}
