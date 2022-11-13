package com.volvo.laundrymanagement.repository;

import com.volvo.laundrymanagement.exception.BookingAlreadyExistException;
import com.volvo.laundrymanagement.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Repository
@Slf4j
public class BookingRepositoryImpl implements BookingRepository {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    private static final String CANCEL_BOOKING = "delete from bookings where booking_code = :booking_code";

    private static final String INSERT_BOOKING = """
            Insert INTO bookings (room_id, resident_id, slot_id, booked_at, booking_code) VALUES (:room_id, :resident_id, :slot_id, :booked_at, :booking_code)
            """;

    private static final String GET_TIMESLOT = "Select slot_id from time_slots where start_time = :start_time and end_time = :end_time";

    private static final String GET_RESIDENT = "Select resident_id from residents where name = :name";

    private static final String GET_LAUNDRY_ROOM = "Select laundry_room_id from laundry_rooms where room_name = :name";

    private static final String GET_ALL_TIME_SLOTS = "Select start_time, end_time from time_slots";

    private static final String GET_ALL_LAUNDRY_ROOMS = "Select room_name from laundry_rooms";

    private static final String GET_BOOKINGS = """ 
            SELECT lr.room_name, ts.start_time, ts.end_time, r.name, b.booked_at
            FROM bookings b
            INNER JOIN time_slots ts ON ts.slot_id = b.slot_id
            INNER JOIN residents r ON r.resident_id = b.resident_id
            INNER JOIN laundry_rooms lr ON lr.laundry_room_id = b.room_id
            WHERE b.booked_at >= :from AND b.booked_at <= :to
            """;

    public BookingRepositoryImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public void cancelBooking(UUID bookingCode) {
        namedParameterJdbcTemplate.update(CANCEL_BOOKING, new MapSqlParameterSource().addValue("booking_code", bookingCode));
    }

    @Override
    public List<BookedSlot> getBookings(LocalDate from, LocalDate to) {
        return namedParameterJdbcTemplate.query(GET_BOOKINGS, new MapSqlParameterSource().addValue("from", from).addValue("to", to),
                new BookedSlotRowMapper());
    }

    @Override
    public UUID bookLaundry(LocalTime startTime, LocalTime endTime, String laundryRoom, LocalDate bookingDate, String residentName, UUID bookingCode) {
        int laundryRoomId = namedParameterJdbcTemplate.queryForObject(GET_LAUNDRY_ROOM,
                new MapSqlParameterSource().addValue("name", laundryRoom), Integer.class);

        int timeSlotId = namedParameterJdbcTemplate.queryForObject(GET_TIMESLOT,
                new MapSqlParameterSource().addValue("start_time", startTime).addValue("end_time", endTime), Integer.class);

        int residentId = namedParameterJdbcTemplate.queryForObject(GET_RESIDENT,
                new MapSqlParameterSource().addValue("name", residentName), Integer.class);

        try {
            namedParameterJdbcTemplate.update(INSERT_BOOKING,
                    new MapSqlParameterSource().addValue("slot_id", timeSlotId)
                            .addValue("room_id", laundryRoomId)
                            .addValue("resident_id", residentId)
                            .addValue("booked_at", bookingDate)
                            .addValue("booking_code", bookingCode));
        } catch (DuplicateKeyException exception) {
            log.error("Booking is already made for slot {}  room {} on date {}", startTime + "-" + endTime, laundryRoom, bookingDate);
            throw new BookingAlreadyExistException("Booking already made");
        }
        return bookingCode;
    }

    @Override
    public List<TimeSlot> getAllTimeSlots() {
        return namedParameterJdbcTemplate.query(GET_ALL_TIME_SLOTS, new TimeSlotRowMapper());

    }

    @Override
    public List<String> getAllLaundryRooms() {
        return namedParameterJdbcTemplate.query(GET_ALL_LAUNDRY_ROOMS, (rs, rowNum) -> rs.getString("room_name"));
    }
}
