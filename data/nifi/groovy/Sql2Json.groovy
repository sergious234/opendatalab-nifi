// package com.iap3

import com.iap3.model.station.parsers.SqlParser
import groovy.json.JsonOutput
import org.apache.nifi.flowfile.FlowFile
import org.apache.nifi.processor.io.OutputStreamCallback


FlowFile flowFile = session.get();
if (!flowFile) {
    flowFile = session.create();
}
if (!flowFile) {
    session.transfer(flowFile, REL_FAILURE)
    return;
}

String content = session.read(flowFile).text;

new SqlParser(content).get_all_stations().ifPresent { stations ->
    try {
        flowFile = session.write(flowFile, { outputStream ->
            outputStream.write(JsonOutput.toJson(stations).bytes)
        } as OutputStreamCallback);
        flowFile = session.putAttribute(flowFile, "mime.type", "application/json")
    }
    catch (Exception ignored) {
        return session.transfer(flowFile, REL_FAILURE)
    } finally {
        return session.transfer(flowFile, REL_SUCCESS)
    }
};

return session.transfer(flowFile, REL_FAILURE)
