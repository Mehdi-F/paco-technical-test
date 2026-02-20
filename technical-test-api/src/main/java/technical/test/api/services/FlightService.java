package technical.test.api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.api.record.FlightRecord;
import technical.test.api.repository.FlightRepository;

@Service
@RequiredArgsConstructor
public class FlightService {
    private final FlightRepository flightRepository;

    public Flux<FlightRecord> getAllFlights() {
        return flightRepository.findAll();
    }

    // Create new flight
    public Mono<FlightRecord> saveFlight(FlightRecord flightRecord) {
        return flightRepository.save(flightRecord);
    }

    // Pagination and Filtering
    public Flux<FlightRecord> getFlightsSortedAndPaginated(Pageable pageable) {
        return flightRepository.findAllBy(pageable);
    }
}
