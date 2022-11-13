package com.volvo.laundrymanagement.model;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

@Slf4j
public class TimeSlotRowMapper implements RowMapper<TimeSlot> {

    @Override
    public TimeSlot mapRow(ResultSet resultSet, int i) throws SQLException {
        return TimeSlot.builder()
                .start(resultSet.getTime("start_time"))
                .end(resultSet.getTime("end_time"))
                .build();
    }
}
