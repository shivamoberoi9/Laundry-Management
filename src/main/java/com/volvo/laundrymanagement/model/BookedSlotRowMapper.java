package com.volvo.laundrymanagement.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class BookedSlotRowMapper implements RowMapper<BookedSlot> {

    @Override
    public BookedSlot mapRow(ResultSet resultSet, int i) throws SQLException {
        return BookedSlot.builder()
                .timeSlot(TimeSlot.builder().start(resultSet.getTime("start_time")).end(resultSet.getTime("end_time")).isBooked(Boolean.TRUE).build())
                .bookedDate(resultSet.getDate("booked_at").toLocalDate())
                .residentName(resultSet.getString("name"))
                .laundryRoom(resultSet.getString("room_name"))
                .build();
    }
}
