package com.volvo.laundrymanagement.service;

import com.volvo.laundrymanagement.model.BookedSlot;
import com.volvo.laundrymanagement.model.TimeSlot;
import com.volvo.laundrymanagement.repository.BookingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Slf4j
public class BookingService {

    private static final int BOOKING_MONTHS = 1;
    private final BookingRepository bookingRepository;

    public BookingService(BookingRepository bookingRepository) {
        this.bookingRepository = bookingRepository;
    }

    public List<BookedSlot> getBookings(LocalDate from, LocalDate to) {
        return bookingRepository.getBookings(from, to);
    }

    public void cancelBooking(UUID bookingCode) {
        bookingRepository.cancelBooking(bookingCode);
    }

    public Map<LocalDate, Map<String, List<TimeSlot>>> getAvailableBookings() {
        log.info("Getting available booking for the period () ", LocalDate.now() + "-" + LocalDate.now().plusMonths(BOOKING_MONTHS));
        var timeSlots = bookingRepository.getAllTimeSlots();
        var laundryRooms = bookingRepository.getAllLaundryRooms();
        var startDate = LocalDate.now();
        var endDate = LocalDate.now().plusMonths(BOOKING_MONTHS);
        long daysDiff = ChronoUnit.DAYS.between(startDate, endDate);

        Map<LocalDate, Map<String, List<TimeSlot>>> roomsWithSlots = new HashMap<>();
        for (int i = 0; i <= daysDiff; i++) {
            Map<String, List<TimeSlot>> roomWithSlots = new HashMap<>();
            for (String laundryRoom : laundryRooms) {
                List<TimeSlot> slots = new ArrayList<>();
                for (TimeSlot slot : timeSlots) {
                    slots.add(TimeSlot.builder().start(slot.getStart()).end(slot.getEnd()).isBooked(Boolean.FALSE).build());
                }
                roomWithSlots.put(laundryRoom, slots);
                roomsWithSlots.put(startDate, roomWithSlots);
            }
            startDate = startDate.plusDays(1);
        }

        var bookedSlots = bookingRepository.getBookings(LocalDate.now(), LocalDate.now().plusMonths(1));
        for (BookedSlot bookedSlot : bookedSlots) {
            var bookings = roomsWithSlots.get(bookedSlot.getBookedDate()).get(bookedSlot.getLaundryRoom());
            for (TimeSlot timeSlot : bookings) {
                if (timeSlot.getStart().equals(bookedSlot.getTimeSlot().getStart()) && timeSlot.getEnd().equals(bookedSlot.getTimeSlot().getEnd()))
                    timeSlot.setBooked(Boolean.TRUE);
            }
        }
        return roomsWithSlots;
    }
    @Transactional
    public UUID bookLaundry(String slotStart, String slotEnd, String laundryRoom, String bookingDate, String residentName) {
        String[] start = slotStart.split(":");
        String[] end = slotEnd.split(":");
        LocalDate bookedDate = LocalDate.parse(bookingDate);
        LocalTime startTime = LocalTime.of(Integer.parseInt(start[0]), Integer.parseInt(start[1]), Integer.parseInt(start[2]));
        LocalTime endTime = LocalTime.of(Integer.parseInt(end[0]), Integer.parseInt(end[1]), Integer.parseInt(end[2]));
        UUID bookingCode = UUID.randomUUID();
        bookingRepository.bookLaundry(startTime, endTime, laundryRoom, bookedDate, residentName, bookingCode);
        log.info("Booking is confirmed with a booking code {} ", bookingCode);
        return bookingCode;
    }
}
