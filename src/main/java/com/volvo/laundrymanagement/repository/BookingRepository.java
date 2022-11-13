package com.volvo.laundrymanagement.repository;

import com.volvo.laundrymanagement.model.BookedSlot;
import com.volvo.laundrymanagement.model.TimeSlot;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

public interface BookingRepository {

    void cancelBooking(UUID bookingCode);

    List<BookedSlot> getBookings(LocalDate from, LocalDate to);

    UUID bookLaundry(LocalTime startTime, LocalTime endTime, String laundryRoom, LocalDate bookingDate, String residentName, UUID bookingCode);

    List<TimeSlot> getAllTimeSlots();

    List<String> getAllLaundryRooms();

}
