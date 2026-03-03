package com.station

import groovy.transform.Canonical

@Canonical
class Station {
    String status
    String contract_name
    Integer number
    Long last_update
    String address
    Integer available_bikes
    Position position
    String name
    Integer bike_stands
    Integer available_bike_stands
    Boolean banking
    Boolean bonus
    Double lng
    Double lat
    String fecha_lectura
    String last_update_fecha

    @Canonical
    static class Position {
        Double lng
        Double lat
    }


    String collection_name() {
        if (!last_update_fecha) {
            throw new IllegalStateException("El campo 'lastUpdateFecha' no puede ser nulo o vacío.")
        }

        def year = last_update_fecha.split("/")?.first() // Extraer el año del formato "yyyy/mm/dd"
        if (!year?.isNumber()) {
            throw new IllegalArgumentException("Formato inválido en 'lastUpdateFecha': ${last_update_fecha}")
        }

        return "RAW_${contract_name}_${year}"
    }
}
