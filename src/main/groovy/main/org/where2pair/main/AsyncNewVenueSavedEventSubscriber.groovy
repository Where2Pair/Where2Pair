package org.where2pair.main

import groovyx.gpars.GParsExecutorsPool
import org.where2pair.write.venue.NewVenueSavedEvent
import org.where2pair.write.venue.NewVenueSavedEventSubscriber

import java.util.concurrent.ExecutorService

import static java.util.concurrent.Executors.newCachedThreadPool


class AsyncNewVenueSavedEventSubscriber implements NewVenueSavedEventSubscriber {

    private static final ExecutorService executorService = newCachedThreadPool()
    private final NewVenueSavedEventSubscriber subscriber

    static NewVenueSavedEventSubscriber asAsyncSubscriber(NewVenueSavedEventSubscriber newVenueSavedEventSubscriber) {
        new AsyncNewVenueSavedEventSubscriber(newVenueSavedEventSubscriber)
    }

    private AsyncNewVenueSavedEventSubscriber(NewVenueSavedEventSubscriber subscriber) {
        this.subscriber = subscriber
    }

    @Override
    void notifyNewVenueSaved(NewVenueSavedEvent newVenueSavedEvent) {
        println 'notifying 1'
        GParsExecutorsPool.withExistingPool(executorService) {
        println 'notifying 2'
        println 'notifying 2.1'
            Closure notify = {
                println 'notifying 2.5'
                println subscriber.getClass()
                subscriber.notifyNewVenueSaved(newVenueSavedEvent) }
            notify.callAsync()
        }
    }
}
