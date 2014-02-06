package org.where2pair.venue.integration

import com.mongodb.MongoClient
import com.mongodb.util.JSON
import org.where2pair.venue.persist.MongoService
import spock.lang.Specification

class MongoServiceIntegrationSpec extends Specification {

    public static final String TEST_COLLECTION = 'ACOLLECTION'

    def "saves a collection to mongo db"() {
        given:
        MongoClient mongoClient = new MongoClient('localhost', 27017)
        MongoService mongoService = new MongoService(mongoClient, 'test')

        when:
        mongoService.save(TEST_COLLECTION, '{"hello":"world"}' )

        then:
        def savedDocument = mongoService.find(TEST_COLLECTION)
        def document = JSON.parse(savedDocument)
        document['hello'] == ['world']
    }
}
