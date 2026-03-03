// package com.iap3

import com.iap3.model.mongo.MongoConnection
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes
import org.bson.Document
import org.bson.conversions.Bson

import java.util.logging.Logger

final Logger logger = Logger.getLogger("GroovyLogger")

if (!getBinding().hasVariable("session")) {
    logger.severe("session is null");
    return
}

def flowFile = session.get();

if (!flowFile) {
    flowFile = session.create();
}

if (!flowFile) {
    session.transfer(flowFile, REL_FAILURE)
    return;
}

MongoConnection.getMongoClient().ifPresent { mongoClient ->
    try {
        MongoDatabase database = mongoClient.getDatabase("Bikes")
        String collectionName = flowFile.getAttribute("city_year")

        if (!collectionName) {
            session.transfer(flowFile, REL_FAILURE)
        }

        boolean collectionExists = database.listCollectionNames()
            .contains(collectionName)

        if (!collectionExists) {
            database.createCollection(collectionName)
            MongoCollection<Document> collection = database.getCollection(collectionName)

            Bson index = Indexes.ascending("number", "fecha_lectura");
            collection.createIndex(index, new IndexOptions().unique(true));

            logger.info("Created index for " + collectionName);
            flowFile = session.putAttribute(flowFile, "Created", "true")
        } else {
            flowFile = session.putAttribute(flowFile, "Created", "false")
        }
        session.transfer(flowFile, REL_SUCCESS)
    } catch (Exception e) {
        session.transfer(flowFile, REL_FAILURE)
    } finally {
        mongoClient.close()
    }
}

