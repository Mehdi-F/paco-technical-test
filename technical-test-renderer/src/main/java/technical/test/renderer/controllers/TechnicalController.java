package technical.test.renderer.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import technical.test.renderer.facades.FlightFacade;
import technical.test.renderer.viewmodels.AirportViewModel;
import technical.test.renderer.viewmodels.FlightViewModel;

@Controller
@RequestMapping
@RequiredArgsConstructor
@Slf4j
public class TechnicalController {

    @Autowired
    private FlightFacade flightFacade;

    @GetMapping
    public Mono<String> getIndexPage(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "price") String sortBy,
            final Model model) {
        model.addAttribute("flights", this.flightFacade.getFlights(page, sortBy));
        model.addAttribute("currentPage", page);
        model.addAttribute("currentSort", sortBy);

        return Mono.just("pages/index");
    }

    // Admin View
    @GetMapping("/admin")
    public Mono<String> getAdminPage(final Model model) {
        FlightViewModel newFlight = new FlightViewModel();

        newFlight.setOrigin(new AirportViewModel());
        newFlight.setDestination(new AirportViewModel());

        model.addAttribute("newFlight", newFlight);

        return Mono.just("pages/admin");
    }

    //  Admin Action
    @PostMapping("/admin")
    public Mono<String> createFlight(@ModelAttribute FlightViewModel newFlight) {
        return this.flightFacade.createFlight(newFlight)
                .thenReturn("redirect:/");
    }
}
