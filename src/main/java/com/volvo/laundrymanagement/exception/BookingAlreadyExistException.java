package com.volvo.laundrymanagement.exception;

public class BookingAlreadyExistException extends RuntimeException {
    public BookingAlreadyExistException(String errorMessage) {
        super(errorMessage);
    }
}
