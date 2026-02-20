package technical.test.renderer.clients;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import technical.test.renderer.properties.TechnicalApiProperties;
import technical.test.renderer.viewmodels.FlightViewModel;

@Component
@Slf4j
public class TechnicalApiClient {

    private final TechnicalApiProperties technicalApiProperties;
    private final WebClient webClient;

    public TechnicalApiClient(TechnicalApiProperties technicalApiProperties, final WebClient.Builder webClientBuilder) {
        this.technicalApiProperties = technicalApiProperties;
        this.webClient = webClientBuilder.build();
    }

    public Flux<FlightViewModel> getFlights(int page, String sortBy) {
        String fullUri = technicalApiProperties.getUrl() + technicalApiProperties.getFlightPath() + "?page={page}&sortBy={sortBy}";

        return webClient
                .get()
                .uri(fullUri, page, sortBy)
                .retrieve()
                .bodyToFlux(FlightViewModel.class);
    }

    public Mono<FlightViewModel> createFlight(FlightViewModel newFlight) {
        String fullUri = technicalApiProperties.getUrl() + technicalApiProperties.getFlightPath();

        return webClient
                .post()
                .uri(fullUri)
                .bodyValue(newFlight)
                .retrieve()
                .bodyToMono(FlightViewModel.class);
    }
}
