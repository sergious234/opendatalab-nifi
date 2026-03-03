package com.station.parsers
import com.station.Station

import java.text.SimpleDateFormat
import java.util.logging.Logger

class SqlParser {
    private String input;
    private Iterator<String> insert;
    final static Logger logger = Logger.getLogger("AddIndex Logger")
    final static SimpleDateFormat date_formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
    SqlParser(String input) {
        this.input = input;
        def lines = input.lines().iterator();

        String next = lines.next();

        try {
            while (!next.contains("INSERT")) {
                next = lines.next()
            }
        } catch (NoSuchElementException ignored) {
            SqlParser.logger.severe("No insert found in this .sql file")
        }
        this.insert = next.iterator();
    }

    private Optional<String> get_next_value() {
        def buffer = ""

        try {
            def rune = this.insert.next();

            while (rune != '(') {
                rune = this.insert.next()
            }
            rune = this.insert.next();
            while (rune != ')') {
                buffer += rune;
                rune = this.insert.next()
            }
            return Optional.of(buffer)
        } catch (NoSuchElementException ignored) {
            return Optional.empty()
        }
    }

    Optional<Station> next_station() {
        try {
            def next_insert = this.get_next_value();
            def values = next_insert.get().split(",");
            Long last_update = date_formatter.parse(values[4].replace("'", "")).getTime()
            return Optional.of(new Station(
                    status: values[1].replace("'", ""),
                    contract_name: values[2].replace("'", ""),
                    number: values[3].toInteger(),
                    last_update: last_update,
                    fecha_lectura: values[5].replace("'", ""),
                    bike_stands: values[6].toInteger(),
                    available_bike_stands: values[7].toInteger(),
                    available_bikes: values[8].toInteger(),
                    last_update_fecha: "2024/11/28"
            ))
        } catch (NoSuchElementException ignored) {
            return Optional.empty();
        }
    }

    Optional<List<Station>> get_all_stations() {
        Optional<Station> station = this.next_station();
        List stations = new ArrayList<>();
        while (station.present) {
            stations.add(station.get())
            station = this.next_station()
        }
        return Optional.of(stations as List<Station>);
    }
}
