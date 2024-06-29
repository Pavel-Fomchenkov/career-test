package com.gridnine.testing.service;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.model.Segment;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class FlightServiceImpl implements FlightService {
    @Override
    public Flight createFlight(final LocalDateTime... dates) {
        if ((dates.length % 2) != 0) {
            throw new IllegalArgumentException(
                    "you must pass an even number of dates");
        }
        List<Segment> segments = new ArrayList<>(dates.length / 2);
        for (int i = 0; i < (dates.length - 1); i += 2) {
            segments.add(new Segment(dates[i], dates[i + 1]));
        }
        return new Flight(segments);
    }

    @Override
    public List<Flight> futureDepartureFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now()))
                ).collect(Collectors.toList());
    }

    @Override
    public List<Flight> departureBeforeArrivalFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isAfter(segment.getArrivalDate()))
                ).collect(Collectors.toList());
    }

    @Override
    public List<Flight> lessOrEqualTimeWaitingFlights(List<Flight> flights, int hours) {
        return flights.stream()
                .filter(flight -> {
                    long totalDelay = 0L;
                    LocalDateTime arrival = null;
                    for (Segment segment : flight.getSegments()) {
                        if (arrival != null) {
                            totalDelay += ChronoUnit.HOURS.between(arrival, segment.getDepartureDate());
                            if (totalDelay > (long) hours) {
                                return false;
                            }
                        }
                        arrival = segment.getArrivalDate();
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    @Override
    public List<Flight> notVeryLongFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> ChronoUnit.HOURS.between(segment.getDepartureDate(), segment.getArrivalDate()) > 15)
                ).collect(Collectors.toList());
    }

    @Override
    public List<Flight> progressivelyOrderedFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight -> {
                    LocalDateTime arrival = null;
                    for (Segment segment : flight.getSegments()) {
                        if (arrival != null) {
                            if (segment.getDepartureDate().isBefore(arrival)) {
                                return false;
                            }
                        }
                        arrival = segment.getArrivalDate();
                    }
                    return true;
                }).collect(Collectors.toList());
    }

    @Override
    public void printFlights(List<Flight> flights) {
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }
}
