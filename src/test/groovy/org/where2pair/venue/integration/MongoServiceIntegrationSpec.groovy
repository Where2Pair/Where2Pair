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

    def 'saves and retrieves a document from a collection in mongo db'() {
        when:
        def id = mongoService.save(TEST_COLLECTION, '{"hello":"world"}' )
        def retrievedDocument = mongoService.findById(TEST_COLLECTION, id)
        def parsedRetrievedDocument = JSON.parse(retrievedDocument)

        then:
        parsedRetrievedDocument.hello == 'world'
    }

    def 'uses criteria to retrieve a documents from a collection in mongo db'() {
        given:
        def id = UUID.randomUUID()
        def anotherId = UUID.randomUUID()
        mongoService.save(TEST_COLLECTION, "{'_id': '${id}', 'a hello':'a world'}" )
        mongoService.save(TEST_COLLECTION, "{'_id': '${anotherId}', 'another hello':'another world'}" )

        when:
        def foundDocuments = mongoService.find(TEST_COLLECTION, "{'_id': '${id}'}")

        then:
        println foundDocuments
        def documents = JSON.parse(foundDocuments)
        documents.size() == 1

    }
}
