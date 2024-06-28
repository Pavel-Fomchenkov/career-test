package com.gridnine.testing;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.service.FlightService;
import com.gridnine.testing.service.FlightServiceImpl;

import java.util.List;

public class Main {
    private static int hours = 2;

    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        final FlightService flightService = new FlightServiceImpl();

        System.out.println("Без перелетов с вылетом до текущего момента времени.");
        flightService.printFlights(flightService.futureDepartureFlights(flights));

        System.out.println("Без перелетов с прилетом раньше вылета.");
        flightService.printFlights(flightService.departureBeforeArrivalFlights(flights));

        System.out.println("Без перелетов с ожиданием более " + hours + " часов.");
        flightService.printFlights(flightService.lessOrEqualTimeWaitingFlights(flights, hours));
    }
}