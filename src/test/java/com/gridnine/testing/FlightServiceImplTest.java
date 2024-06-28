package com.gridnine.testing;

import com.gridnine.testing.model.Flight;
import com.gridnine.testing.service.FlightService;
import com.gridnine.testing.service.FlightServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FlightServiceImplTest {
    private final FlightService out = new FlightServiceImpl();
    private final List<Flight> flights = new ArrayList<>();

    //        вылет до текущего времени
    private final Flight flight1 = out.createFlight(
            LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
    private final Flight flight2 = out.createFlight(
            LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(1),
            LocalDateTime.now().minusDays(1), LocalDateTime.now().plusDays(1));
    //        дата прилета в сегменте раньше даты вылета
    private final Flight flight3 = out.createFlight(
            LocalDateTime.now().plusMinutes(5), LocalDateTime.now().minusHours(1),
            LocalDateTime.now().plusHours(1), LocalDateTime.now().plusDays(4));
    private final Flight flight4 = out.createFlight(
            LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(2), LocalDateTime.now());
    //        Перелеты, где общее время, проведённое на земле, превышает два часа
    //        (время на земле — это интервал между прилётом одного сегмента и вылетом следующего за ним).
    private final Flight flight5 = out.createFlight(
            LocalDateTime.now().plusMinutes(5), LocalDateTime.now().plusHours(1),
            LocalDateTime.now().plusHours(4), LocalDateTime.now().plusDays(1));
    //        Нормальный перелет из одного сегмента
    private final Flight flight6 = out.createFlight(
            LocalDateTime.now().plusMinutes(30), LocalDateTime.now().plusHours(2));
    //        Нормальный перелет из нескольких сегментов
    private final Flight flight7 = out.createFlight(
            LocalDateTime.now().plusMinutes(45), LocalDateTime.now().plusHours(3),
            LocalDateTime.now().plusHours(4), LocalDateTime.now().plusDays(5));
    //        с датой вылета раньше даты прилета в предыдущем сегменте
    private final Flight flight8 = out.createFlight(
            LocalDateTime.now().plusMinutes(45), LocalDateTime.now().plusHours(3),
            LocalDateTime.now().plusHours(2), LocalDateTime.now().plusDays(1));

    @BeforeEach
    private void setTestData() {
        flights.clear();
        flights.addAll(List.of(flight1, flight2, flight3, flight4, flight5, flight6, flight7, flight8));
    }

    @Test
    public void createFlightShouldWork() {
        Flight flight = out.createFlight(LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1));
        assertTrue(flight.getSegments().get(0).getDepartureDate().isEqual(LocalDateTime.now().minusHours(2)) &&
                flight.getSegments().get(0).getArrivalDate().isEqual(LocalDateTime.now().plusHours(1)));
    }

    @Test
    public void shouldExcludeFlightsWithDepartureBeforeNow() {
        List<Flight> expected = out.futureDepartureFlights(flights);
        assertTrue(expected.size() == 6 &&
                expected.containsAll(List.of(flight3, flight4, flight5, flight6, flight7, flight8))
        );
    }

    @Test
    public void shouldExcludeFlightsWithArrivalBeforeDeparture() {
        List<Flight> expected = out.departureBeforeArrivalFlights(flights);
        assertTrue(expected.size() == 6 &&
                expected.containsAll(List.of(flight1, flight2, flight5, flight6, flight7, flight8))
        );
    }

    @Test
    public void shouldExcludeFlightsWithTotalHoursSpendWaitingBetweenIsMoreThan() {
        List<Flight> expected = out.lessOrEqualTimeWaitingFlights(flights, 2);
        assertTrue(expected.size() == 7 &&
                expected.containsAll(List.of(flight1, flight2, flight3, flight4, flight6, flight7, flight8))
        );
    }

}