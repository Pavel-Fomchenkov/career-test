package com.gridnine.testing;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    private static int hours = 2;

    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();

        System.out.println("Все полеты.");
        printFlights(flights);

        System.out.println("Без перелетов с вылетом до текущего момента времени.");
        printFlights(futureDepartureFlights(flights));
        System.out.println("Повтор");
        printFlights(fDF(flights));


        System.out.println("Без перелетов с прилетом раньше вылета.");
        printFlights(departureBeforeLandingFlights(flights));
        System.out.println("Повтор");
        printFlights(dBLF(flights));

        System.out.println("Без перелетов с ожиданием более 2 часов.");
        printFlights(lessTimeWaitingFlights(flights, hours));
        System.out.println("Повтор");
        printFlights(lTWF(flights, hours));
    }

    //    Вылет до текущего момента времени.
    private static List<Flight> futureDepartureFlights(List<Flight> flights) {
        List<Flight> list = new ArrayList<>(flights);
        for (int i = list.size() - 1; i >= 0; i--) {
            for (Segment segment : list.get(i).getSegments()) {
                if (segment.getDepartureDate().isBefore(LocalDateTime.now())) {
                    list.remove(i);
                    break;
                }
            }
        }
        return list;
    }

    //    Сегменты с датой прилёта раньше даты вылета.
    private static List<Flight> departureBeforeLandingFlights(List<Flight> flights) {
        List<Flight> list = new ArrayList<>(flights);
        for (int i = list.size() - 1; i >= 0; i--) {
            for (Segment segment : list.get(i).getSegments()) {
                if (segment.getDepartureDate().isAfter(segment.getArrivalDate())) {
                    list.remove(i);
                    break;
                }
            }
        }
        return list;
    }

    //    Перелеты, где общее время, проведённое на земле, превышает два часа
    //    (время на земле — это интервал между прилётом одного сегмента и вылетом следующего за ним).
    private static List<Flight> lessTimeWaitingFlights(List<Flight> flights, int hours) {
        List<Flight> list = new ArrayList<>(flights);
        for (int i = list.size() - 1; i >= 0; i--) {
            long totalDelay = 0L;
            LocalDateTime arrival = null;
            for (Segment segment : list.get(i).getSegments()) {
                if (arrival != null) {
                    totalDelay += ChronoUnit.HOURS.between(arrival, segment.getDepartureDate());
                    if (totalDelay > (long) hours) {
                        list.remove(i);
                        break;
                    }
                }
                arrival = segment.getArrivalDate();
            }
        }
        return list;
    }

    private static void printFlights(List<Flight> flights) {
        for (Flight flight : flights) {
            System.out.println(flight);
        }
    }

    private static List<Flight> fDF(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isBefore(LocalDateTime.now()))
                ).collect(Collectors.toList());
    }

    private static List<Flight> dBLF(List<Flight> flights) {
        return flights.stream()
                .filter(flight ->
                        flight.getSegments().stream()
                                .noneMatch(segment -> segment.getDepartureDate().isAfter(segment.getArrivalDate()))
                ).collect(Collectors.toList());
    }

    private static List<Flight> lTWF(List<Flight> flights, int hours) {
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

}