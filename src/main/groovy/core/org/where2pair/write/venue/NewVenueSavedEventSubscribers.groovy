package org.where2pair.write.venue

import groovy.transform.Immutable

@Immutable
class NewVenueSavedEventSubscribers {

    private List<NewVenueSavedEventSubscriber> subscribers

    void publish(NewVenueSavedEvent newVenueSavedEvent) {
        subscribers.each {
            it.notifyNewVenueSaved(newVenueSavedEvent)
        }
    }
}
