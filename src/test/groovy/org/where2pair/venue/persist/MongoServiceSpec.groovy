package org.where2pair.venue.persist

import com.mongodb.DB
import com.mongodb.DBCollection
import com.mongodb.DBCursor
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.util.JSON
import spock.lang.Specification

class MongoServiceSpec extends Specification {

    private static final String USERNAME = "username"
    private static final String PASSWORD = "password"
    private static final String DB_NAME = "db name"
    private static final String COLLECTION_NAME = "collectionName"
    private static final String JSON_DOCUMENT = '{"test" : "this"}'
    private static final String DIFFERENT_JSON_DOCUMENT = '{"test" : "this"}'
    private DBObject sampleResultObject
    private DBObject differentSampleResultObject
    private DB mongoDb
    private MongoClient mongoClient
    private DBCollection collection
    private DBCursor dbCursor

    def setup() {
        mongoClient = Mock()
        mongoDb = Mock()
        mongoClient.getDB(DB_NAME) >> mongoDb
        collection = Mock()
        dbCursor = Mock()
        sampleResultObject = asDBObject(JSON_DOCUMENT)
        differentSampleResultObject = asDBObject(DIFFERENT_JSON_DOCUMENT)
    }


    def "uses mongo api to establish connection"() {
        when:
        new MongoService(mongoClient, USERNAME, PASSWORD, DB_NAME)

        then:
        1 * mongoDb.authenticate(USERNAME, PASSWORD.toCharArray()) >> true
    }

    def "throws exception if authentication fails"() {
        when:
        new MongoService(mongoClient, USERNAME, PASSWORD, DB_NAME)

        then:
        thrown(RuntimeException)
    }


    def "gets the full collection by specifying empty criteria"() {
        given:
        MongoService mongoService = setupMongoServiceToSuccessfullyAuthenticate()
        mongoDb.getCollection(COLLECTION_NAME) >> collection

        when:
        mongoService.find(COLLECTION_NAME)

        then:
        collection.find(asDBObject('{}')) >> dbCursor
    }

    def "get the first match in the specified collection"() {
        given:
        MongoService mongoService = setupMongoServiceToSuccessfullyAuthenticate()
        def criteria = JSON_DOCUMENT
        mongoDb.getCollection(COLLECTION_NAME) >> collection

        when:
        mongoService.findOne(COLLECTION_NAME, criteria)

        then:
        1 * collection.findOne(asDBObject(criteria))
    }

    def "finds documents in collection matching the specified criteria"() {
        given:
        MongoService mongoService = setupMongoServiceToSuccessfullyAuthenticate()
        def criteria = JSON_DOCUMENT
        mongoDb.getCollection(COLLECTION_NAME) >> collection

        when:
        mongoService.find(COLLECTION_NAME, criteria)

        then:
        1 * collection.find(asDBObject(criteria)) >> dbCursor
    }

    def "converts the returned collection into JSON"() {
        given:
        MongoService mongoService = setupMongoServiceToSuccessfullyAuthenticate()
        prepareMockToReturnSampleResult()

        when:
        String result = mongoService.find(COLLECTION_NAME)

        then:
        result == "[${sampleResultObject.toString()},${differentSampleResultObject.toString()}]"
    }

    def "stores a document to an existing collection"() {
        given:
        MongoService mongoService = setupMongoServiceToSuccessfullyAuthenticate()
        mongoDb.getCollection(COLLECTION_NAME) >> collection
        String document = "[$JSON_DOCUMENT]"

        when:
        mongoService.save(COLLECTION_NAME, document)

        then:
        1 * collection.save(asDBObject(document))
    }

    private void prepareMockToReturnSampleResult() {
        mongoDb.getCollection(COLLECTION_NAME) >> collection
        collection.find(_) >> dbCursor
        dbCursor.hasNext() >>> [true, true, false]
        dbCursor.next() >>> [sampleResultObject, differentSampleResultObject]
    }

    private MongoService setupMongoServiceToSuccessfullyAuthenticate() {
        mongoDb.authenticate(USERNAME, PASSWORD.toCharArray()) >> true
        new MongoService(mongoClient, USERNAME, PASSWORD, DB_NAME)
    }

    private DBObject asDBObject(String criteria) {
        JSON.parse(criteria) as DBObject
    }
}