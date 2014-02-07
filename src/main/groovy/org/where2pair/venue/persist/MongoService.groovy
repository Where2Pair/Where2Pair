package org.where2pair.venue.persist

import com.mongodb.DB
import com.mongodb.DBObject
import com.mongodb.MongoClient
import com.mongodb.WriteResult
import com.mongodb.util.JSON

class MongoService {
    private DB mongoDb;

    public MongoService(MongoClient client, String dbName) {
        mongoDb = client.getDB(dbName)
    }

    public MongoService(MongoClient client, String username, String password, String dbName) {
        mongoDb = client.getDB(dbName)
        if (!mongoDb.authenticate(username, password.toCharArray())) {
            throw new RuntimeException("Authentication to DB failed.")
        }
    }

    public String find(String collection, String criteria = '{}') {
        def cursor = mongoDb.getCollection(collection).find(asDBObject(criteria))
        def items = []
        try {
            while (cursor.hasNext()) {
                items << JSON.serialize(cursor.next())
            }
        } finally {
            cursor.close()
        }
        "[${items.join(',')}]"
    }

    public String findOne(String collection, String criteria = '{}') {
        def result = mongoDb.getCollection(collection).findOne(asDBObject(criteria))
        result ? JSON.serialize(result) : null
    }

    public String save(String collectionName, String document) {
        def objectToSave = JSON.parse(document) as DBObject
        mongoDb.getCollection(collectionName).save(objectToSave)
        objectToSave._id
    }

    public String findById(String collectionName, String id) {
        findOne(collectionName, "{'_id': {'\$oid': '${id}'}}")
    }

    private DBObject asDBObject(String jsonString) {
        def result
        try {
            result = JSON.parse(jsonString) as DBObject
        } catch (Exception e) {
            e.printStackTrace()
        }
        result
    }

    private void update(String collectionName, String id, String document) {
        DBObject objectToUpdate = JSON.parse("{'_id': {'\$oid': '${id}'}}") as DBObject
        DBObject updateWith = JSON.parse(document)
        mongoDb.getCollection(collectionName).update(objectToUpdate, updateWith)
    }
}
