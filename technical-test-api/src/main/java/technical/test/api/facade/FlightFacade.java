package technical.test.api.facade;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.mapper.AirportMapper;
import technical.test.api.mapper.FlightMapper;
import technical.test.api.record.AirportRecord;
import technical.test.api.record.FlightRecord;
import technical.test.api.representation.FlightRepresentation;
import technical.test.api.services.AirportService;
import technical.test.api.services.FlightService;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class FlightFacade {
    private final FlightService flightService;
    private final AirportService airportService;
    private final FlightMapper flightMapper;
    private final AirportMapper airportMapper;

    public Flux<FlightRepresentation> getAllFlights() {
        return this.enrichFlightStream(flightService.getAllFlights());
    }

    public Mono<FlightRepresentation> createFlight(FlightRepresentation flightRepresentation) {
        FlightRecord record = flightMapper.convert(flightRepresentation);
        if (record.getId() == null) {
            record.setId(UUID.randomUUID());
        }
        return flightService.saveFlight(record)
                .flatMap(this::enrichSingleFlight);
    }

    public Flux<FlightRepresentation> getPaginatedFlights(Pageable pageable) {
        return this.enrichFlightStream(flightService.getFlightsSortedAndPaginated(pageable));
    }

    // Helper to keep code DRY
    private Flux<FlightRepresentation> enrichFlightStream(Flux<FlightRecord> flightRecordFlux) {
        return flightRecordFlux.concatMap(this::enrichSingleFlight);
    }

    private Mono<FlightRepresentation> enrichSingleFlight(FlightRecord flightRecord) {
        return airportService.findByIataCode(flightRecord.getOrigin())
                .zipWith(airportService.findByIataCode(flightRecord.getDestination()))
                .map(tuple -> {
                    FlightRepresentation rep = this.flightMapper.convert(flightRecord);
                    rep.setOrigin(this.airportMapper.convert(tuple.getT1()));
                    rep.setDestination(this.airportMapper.convert(tuple.getT2()));
                    return rep;
                });
    }
}
