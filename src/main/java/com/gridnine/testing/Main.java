package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static int hours = 2;

    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        System.out.println("Без перелетов с вылетом до текущего момента времени.");
        printFlights(futureDepartureFlights(flights));

        System.out.println("Без перелетов с прилетом раньше вылета.");
        printFlights(departureBeforeLandingFlights(flights));

        System.out.println("Без перелетов с ожиданием более " + hours + " часов.");
        printFlights(lessTimeWaitingFlights(flights, hours));
    }

    private static List<Flight> futureDepartureFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now()))
                ).collect(Collectors.toList());
    }

    private static List<Flight> departureBeforeLandingFlights(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isAfter(segment.getArrivalDate()))
                ).collect(Collectors.toList());
    }

    private static List<Flight> lessTimeWaitingFlights(List<Flight> flights, int hours) {
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

    private static void printFlights(List<Flight> flights) {
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }
}