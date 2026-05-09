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
flowFile = session.putAttribute(flowFile, "Hola", "Mundo")
session.transfer(flowFile, REL_SUCCESS)


