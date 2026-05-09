// package com.iap3

// import com.iap3.model.mongo.MongoConnection
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.IndexOptions
import com.mongodb.client.model.Indexes

import com.mongodb.client.MongoClient
import com.mongodb.client.MongoClients

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


class MongoConnection {
		private static final String username = "root"
		private static final String password = "example"
		private static final String uri = String.format("mongodb://%s:%s@mongodb:27017", username, password);

	static Optional<MongoClient> getMongoClient() {
		try {
			MongoClient mongoClient = MongoClients.create(uri);
			return Optional.ofNullable(mongoClient);
		} catch (Exception ignored) {
			return Optional.empty()
		}
	}
}

MongoConnection.getMongoClient().ifPresent { mongoClient ->
    try {
        MongoDatabase database = mongoClient.getDatabase("Bikes");
        String collectionName = flowFile.getAttribute("city_year");

        if (!collectionName) {
            session.transfer(flowFile, REL_FAILURE)
        }

        boolean collectionExists = database.listCollectionNames()
            .contains(collectionName)

        if (!collectionExists) {
            database.createCollection(collectionName);
						flowFile = session.putAttribute(flowFile, "Created", "true");
        } else {
            flowFile = session.putAttribute(flowFile, "Created", "false");
        }

				MongoCollection<Document> collection = database.getCollection(collectionName);

				Bson index = Indexes.ascending("number", "fecha_lectura");
				collection.createIndex(index, new IndexOptions().unique(true));

				logger.info("Created index for " + collectionName);

        session.transfer(flowFile, REL_SUCCESS)
    } catch (Exception e) {
        flowFile = session.putAttribute(flowFile, "Exception", e.toString())
        session.transfer(flowFile, REL_FAILURE)
    } finally {
        mongoClient.close()
    }
}

