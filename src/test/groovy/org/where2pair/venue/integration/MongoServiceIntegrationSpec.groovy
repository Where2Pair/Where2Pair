package org.where2pair.venue.integration

import com.mongodb.MongoClient
import com.mongodb.util.JSON
import org.where2pair.venue.persist.MongoService
import spock.lang.Shared
import spock.lang.Specification

class MongoServiceIntegrationSpec extends Specification {

    public static final String TEST_COLLECTION = 'A_COLLECTION'
    @Shared MongoService mongoService

    def setupSpec() {
        MongoClient mongoClient = new MongoClient('localhost', 27017)
        mongoService = new MongoService(mongoClient, 'test')
    }

    def 'saves and retrieves a document from a collection in mongo db'() {
        when:
        def id = mongoService.save(TEST_COLLECTION, '{"hello":"world"}')
        def retrievedDocument = mongoService.findById(TEST_COLLECTION, id)
        def parsedRetrievedDocument = JSON.parse(retrievedDocument)

        then:
        parsedRetrievedDocument.hello == 'world'
    }

    def 'uses criteria to retrieve a documents from a collection in mongo db'() {
        given:
        def uniqueCriteria = UUID.randomUUID()
        def anotherUniqueCriteria = UUID.randomUUID()
        mongoService.save(TEST_COLLECTION, "{'attribute':'$uniqueCriteria'}")
        mongoService.save(TEST_COLLECTION, "{'attribute':'$anotherUniqueCriteria'}")

        when:
        def foundDocuments = mongoService.find(TEST_COLLECTION, "{'attribute': '$uniqueCriteria'}")

        then:
        def documents = JSON.parse(foundDocuments)
        documents.size() == 1
    }

    def 'updates an existing document'() {
        given:
        def id = mongoService.save(TEST_COLLECTION, "{'attribute':'a world'}")

        when:
        mongoService.update(TEST_COLLECTION, id, "{'attribute':'another world'}")

        then:
        def foundDocument = mongoService.findById(TEST_COLLECTION, id)
        def document = JSON.parse(foundDocument)
        document != null
        document.attribute == 'another world'
    }
}
