package com.station

import org.bson.Document

class StationUtils {
    static Document stationToDocument(Station station) {
        return new Document()
                .append("status", station.status)
                .append("contract_name", station.contract_name)
                .append("number", station.number)
                .append("last_update", station.last_update)
//                .append("add_date", station.addDate)
//                .append("stands", station.stands)
//                .append("available_stands", station.availableStands)
                .append("available_bikes", station.available_bikes)
                .append("position", new Document()
                        .append("lng", station.position?.lng)
                        .append("lat", station.position?.lat))
                .append("name", station.name)
                .append("address", station.address)
                .append("bike_stands", station.bike_stands)
                .append("available_bike_stands", station.available_bike_stands)
                .append("banking", station.banking)
                .append("bonus", station.bonus)
                .append("lng", station.lng)
                .append("lat", station.lat)
                .append("fecha_lectura", station.fecha_lectura)
                .append("last_update_fecha", station.last_update_fecha)
    }

    static Station documentToStation(Document doc) {
        return new Station(
                status: doc.getString("status"),
                contract_name: doc.getString("contract_name"),
                number: doc.getInteger("number"),
                last_update: doc.getLong("last_update"),
//                addDate: doc.getString("add_date"),
//                stands: doc.getInteger("stands"),
//                availableStands: doc.getInteger("available_stands"),
                available_bikes: doc.getInteger("available_bikes"),
                position: new Station.Position(
                        lng: doc.get("position")?.getDouble("lng"),
                        lat: doc.get("position")?.getDouble("lat")
                ),
                name: doc.getString("name"),
                address: doc.getString("address"),
                bike_stands: doc.getInteger("bike_stands"),
                available_bike_stands: doc.getInteger("available_bike_stands"),
                banking: doc.getBoolean("banking"),
                bonus: doc.getBoolean("bonus"),
                lng: doc.getDouble("lng"),
                lat: doc.getDouble("lat"),
                fecha_lectura: doc.getString("fecha_lectura"),
                last_update_fecha: doc.getString("last_update_fecha")
        )
    }

    static Station jsonToStation(Object json) {
        return new Station(
                status: json.status,
                contract_name: json.contract_name,
                number: json.number,
                last_update: json.last_update,
//                addDate: json.add_date,
//                stands: json.stands,
//                available_stands: json.available_stands,
                available_bikes: json.available_bikes,
                position: json.position ? new Station.Position(
                        lng: json.position?.lng,
                        lat: json.position?.lat
                ) : null,
                name: json.name,
                address: json.address,
                bike_stands: json.bike_stands,
                available_bike_stands: json.available_bike_stands,
                banking: json.banking,
                bonus: json.bonus,
                lng: json?.lng,
                lat: json?.lat,
                fecha_lectura: json.fecha_lectura,
                last_update_fecha: json.last_update_fecha
        )
    }
}
