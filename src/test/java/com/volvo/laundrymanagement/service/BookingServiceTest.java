package com.volvo.laundrymanagement.service;

import com.volvo.laundrymanagement.model.BookedSlot;
import com.volvo.laundrymanagement.model.TimeSlot;
import com.volvo.laundrymanagement.repository.BookingRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertNotNull;

class BookingServiceTest {

    private BookingService bookingService;
    private BookingRepository bookingRepository;

    private static final int START_SLOT_TIME = 07;
    private static final int END_SLOT_TIME = 10;

    private static final LocalDate BOOKED_DATE = LocalDate.now();
    private static final String LAUNDRY_ROOM = "Room1";
    private static final String RESIDENT_NAME = "shivam";

    @BeforeEach
    void setUp() {
        bookingService = mock(BookingService.class);
        bookingRepository = mock(BookingRepository.class);
        bookingService = new BookingService(bookingRepository);
    }

    @Test()
    void getAvailableBookings() {
        var bookings = createBookings();
        when(bookingRepository.getBookings(BOOKED_DATE, BOOKED_DATE.plusMonths(1))).thenReturn(bookings);
        when(bookingRepository.getAllLaundryRooms()).thenReturn(List.of(LAUNDRY_ROOM));
        when(bookingRepository.getAllTimeSlots()).thenReturn(createTimeSlots());
        var availableBookings = bookingService.getAvailableBookings();
        assertNotNull("There are bookings", availableBookings);
        assertNotNull("There are bookings for the date", availableBookings.get(BOOKED_DATE));
        List<TimeSlot> timeSlots = availableBookings.get(LocalDate.now()).get(LAUNDRY_ROOM);
        for (TimeSlot timeSlot : timeSlots) {
            if (timeSlot.getStart().equals(LocalTime.of(START_SLOT_TIME, 00, 00)) && timeSlot.getEnd().equals(LocalTime.of(END_SLOT_TIME, 00, 00))) {
                assertThat(timeSlot.isBooked() == Boolean.TRUE);
            } else {
                assertThat(timeSlot.isBooked() == Boolean.FALSE);
            }
        }
    }
    private List<BookedSlot> createBookings() {
        var firstTimeSlot = TimeSlot.builder().start(Time.valueOf(LocalTime.of(START_SLOT_TIME, 00, 00))).end(Time.valueOf(LocalTime.of(END_SLOT_TIME, 00, 00))).isBooked(Boolean.TRUE).build();
        var firstBooking = BookedSlot.builder().bookedDate(LocalDate.now()).timeSlot(firstTimeSlot).residentName(RESIDENT_NAME).laundryRoom(LAUNDRY_ROOM).build();
        var secondBooking = BookedSlot.builder().bookedDate(LocalDate.now().plusDays(1)).timeSlot(firstTimeSlot).residentName(RESIDENT_NAME).laundryRoom(LAUNDRY_ROOM).build();
        return List.of(firstBooking, secondBooking);
    }

    private List<TimeSlot> createTimeSlots(){
       return List.of(TimeSlot.builder().start(Time.valueOf(LocalTime.of(START_SLOT_TIME, 00, 00))).end(Time.valueOf(LocalTime.of(END_SLOT_TIME, 00, 00))).isBooked(Boolean.TRUE).build());
    }
}
