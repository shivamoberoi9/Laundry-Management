package com.volvo.laundrymanagement.model;

import lombok.Builder;
import lombok.Data;

import java.sql.Time;


@Builder
@Data
public class TimeSlot {

    Time start;
    Time end;
    boolean isBooked;
}
