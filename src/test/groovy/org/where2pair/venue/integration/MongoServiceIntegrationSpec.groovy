package org.where2pair.venue.integration

import com.mongodb.MongoClient
import com.mongodb.util.JSON
import org.where2pair.venue.persist.MongoService
import spock.lang.Shared
import spock.lang.Specification

class MongoServiceIntegrationSpec extends Specification {

    public static final String TEST_COLLECTION = 'ACOLLECTION'
    @Shared MongoService mongoService

    def setupSpec() {
        MongoClient mongoClient = new MongoClient('localhost', 27017)
        mongoService = new MongoService(mongoClient, 'test')
    }

    def 'saves and retrieves a document from mongo db'() {
        when:
        mongoService.save(TEST_COLLECTION, '{"hello":"world"}' )

        then:
        def savedDocument = mongoService.findOne(TEST_COLLECTION)
        def document = JSON.parse(savedDocument)
        document.hello == 'world'
    }

    def 'uses criteria to retrieve a collection from mongo db'() {
        given:
        mongoService.save(TEST_COLLECTION, '{"a hello":"a world"}' )
        mongoService.save(TEST_COLLECTION, '{"another hello":"another world"}' )

        when:
        def foundDocuments = mongoService.find(TEST_COLLECTION, '{"a hello":"a world"}')

        then:
        println foundDocuments
        def document = JSON.parse(foundDocuments)
        document['hello'].size() == 1

    }
}
