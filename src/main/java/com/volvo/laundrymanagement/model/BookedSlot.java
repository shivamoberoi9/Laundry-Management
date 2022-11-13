package com.volvo.laundrymanagement.model;


import lombok.Builder;
import lombok.Value;

import java.time.LocalDate;

@Value
@Builder
public class BookedSlot {

    LocalDate bookedDate;
    String residentName;
    String laundryRoom;
    TimeSlot timeSlot;

}
