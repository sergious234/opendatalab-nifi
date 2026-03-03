// package com.iap3

import com.iap3.model.mongo.MongoConnection
import com.mongodb.client.MongoCollection
import com.mongodb.client.MongoDatabase
import com.mongodb.client.model.Filters
import groovy.json.JsonSlurper
import org.apache.nifi.flowfile.FlowFile
import org.bson.Document
import com.iap3.model.station.Station
import com.iap3.model.station.StationUtils

import java.util.logging.Logger

final Logger logger = Logger.getLogger("GroovyLogger")

// ProcessSession session;
FlowFile flowFile = session.get();
if (!flowFile) {
    flowFile = session.create();
}
if (!flowFile) {
    session.transfer(flowFile, REL_FAIL)
    return;
}

String json_string = session.read(flowFile).text;
String collection_name = flowFile.getAttribute("city_year");

MongoConnection.getMongoClient().ifPresent { mongoClient ->
    try {
        def json = new JsonSlurper().parseText(json_string);
        String uri = String.format("mongodb://%s:%s@localhost:27017", username, password)
        MongoDatabase database = mongoClient.getDatabase("Bikes")
        MongoCollection<Document> collection = database.getCollection(collection_name)

        int updates_entries = 0;


        updated_entries = json.iterator()
            .toList()
            .stream()
            .map { json_object ->
                try {
                    def station = StationUtils.jsonToStation(json_object);
                    Document match = collection.find(
                        Filters.and(
                            Filters.eq("number", station.number),
                            Filters.eq("fecha_lectura", station.fecha_lectura)
                        )
                    ).first();
                    return match
                } catch (Exception e) {
                    logger.severe("Error reading a station: " + e.toString() + "\n" + json_object.toString())
                    return null;
                }
            }
            .filter { obj -> obj != null }
            .flatMap { match ->
                try {
                    Station mongo_station = StationUtils.documentToStation(match);
                    // Update attrs
                    return 1;
                } catch (Exception e) {
                    logger.severe("Error retreaving data from mongo: " + e.toString());
                    return 0;
                }
            }
            .inject(0) { acc, num -> acc + num };

        flowFile = session.putAttribute(flowFile, "updates_entries", updates_entries.toString())
        session.transfer(flowFile, REL_SUCCESS);

    } catch (Exception e) {
        logger.severe("UpdatingError in: " + collection_name + ", " + e.toString());
        flowFile = session.putAttribute(flowFile, "Failed", e.toString())
        session.transfer(flowFile, REL_FAILURE)
    } finally {
        if (mongoClient) {
            mongoClient.close();
        }
    }
}





