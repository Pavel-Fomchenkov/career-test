package com.gridnine.testing.service;

import com.gridnine.testing.model.Flight;

import java.time.LocalDateTime;
import java.util.List;

public interface FlightService {
    Flight createFlight(final LocalDateTime... dates);

    List<Flight> futureDepartureFlights(List<Flight> flights);

    List<Flight> departureBeforeArrivalFlights(List<Flight> flights);

    List<Flight> lessOrEqualTimeWaitingFlights(List<Flight> flights, int hours);

    List<Flight> notVeryLongFlights(List<Flight> flights);

    List<Flight> progressivelyOrderedFlights(List<Flight> flights);

    void printFlights(List<Flight> flights);

    FlightServiceImpl.FlightFilterBuilder filterBuilder(List<Flight> flights);
}
