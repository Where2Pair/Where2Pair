package org.where2pair.venue.integration

import com.mongodb.MongoClient
import org.where2pair.venue.persist.MongoService
import spock.lang.Specification

class MongoServiceIntegrationSpec extends Specification {
    def "saves a collection to mongo db"() {
        given:
        MongoClient mongoClient = new MongoClient('localhost', 27017)
        MongoService mongoService = new MongoService(mongoClient, 'test')

        when:
        mongoService.save('ACOLLECTION', '{"hello":"world"}' )

        then:
        mongoService.find('ACOLLECTION') == 'WHATEVER'
    }
}
