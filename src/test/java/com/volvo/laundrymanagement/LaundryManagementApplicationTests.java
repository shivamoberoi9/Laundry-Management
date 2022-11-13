package com.volvo.laundrymanagement;

import com.volvo.laundrymanagement.exception.BookingAlreadyExistException;
import com.volvo.laundrymanagement.model.BookedSlot;
import com.volvo.laundrymanagement.model.TimeSlot;
import com.volvo.laundrymanagement.service.BookingService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;

import java.time.LocalDate;
import java.time.LocalTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@RunWith(SpringRunner.class)
@SpringBootTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.BEFORE_EACH_TEST_METHOD)
@ActiveProfiles("test")
class LaundryManagementApplicationTests {
    @Autowired
    BookingService bookingService;
    private static final String START_SLOT_TIME = "07:00:00";
    private static final String END_SLOT_TIME = "10:00:00";
    private static final String BOOKED_DATE = LocalDate.now().toString();
    private static final String LAUNDRY_ROOM = "Room1";
    private static final String RESIDENT_NAME = "shivam";

    @Test
    void getBookings() {
        bookingService.bookLaundry(START_SLOT_TIME, END_SLOT_TIME, LAUNDRY_ROOM, BOOKED_DATE, RESIDENT_NAME);
        var bookings = bookingService.getBookings(LocalDate.now(), LocalDate.now().plusMonths(1));
        for (BookedSlot bookedSlot : bookings) {
            assertThat(bookedSlot.getTimeSlot().isBooked() == Boolean.TRUE);
            assertThat(bookedSlot.getBookedDate().equals(BOOKED_DATE));
            assertThat(bookedSlot.getLaundryRoom().equals(LAUNDRY_ROOM));
            assertThat(bookedSlot.getResidentName().equals(RESIDENT_NAME));
            assertThat(bookedSlot.getTimeSlot().getStart().equals(LocalTime.of(07, 00, 00)));
            assertThat(bookedSlot.getTimeSlot().getEnd().equals(LocalTime.of(10, 00, 00)));
        }
    }

    @Test
    void getAvailableBookings_whenSlotBooked() {
        bookingService.bookLaundry(START_SLOT_TIME, END_SLOT_TIME, LAUNDRY_ROOM, BOOKED_DATE, RESIDENT_NAME);
        var result = bookingService.getAvailableBookings();
        var timeSlots = result.get(LocalDate.parse(BOOKED_DATE)).get(LAUNDRY_ROOM);
        for (TimeSlot timeSlot : timeSlots) {
            if (timeSlot.getStart().equals(LocalTime.of(07, 00, 00)) && timeSlot.getEnd().equals(LocalTime.of(10, 00, 00))) {
                assertThat(timeSlot.isBooked() == Boolean.TRUE);
            } else {
                assertThat(timeSlot.isBooked() == Boolean.FALSE);
            }
        }
    }

    @Test
    void cancelBooking() {
        var bookingCode = bookingService.bookLaundry(START_SLOT_TIME, END_SLOT_TIME, LAUNDRY_ROOM, BOOKED_DATE, RESIDENT_NAME);
        bookingService.cancelBooking(bookingCode);
        var bookings = bookingService.getBookings(LocalDate.now(), LocalDate.now());
        assertThat(bookings.isEmpty());
    }

    @Test
    void throwBookingAlreadyExistException_whenBookingForSameSlotRoomAndDate() {
        bookingService.bookLaundry(START_SLOT_TIME, END_SLOT_TIME, LAUNDRY_ROOM, BOOKED_DATE, RESIDENT_NAME);
        assertThrows(BookingAlreadyExistException.class, () -> {
            bookingService.bookLaundry(START_SLOT_TIME, END_SLOT_TIME, LAUNDRY_ROOM, BOOKED_DATE, RESIDENT_NAME);
        });
    }
}
