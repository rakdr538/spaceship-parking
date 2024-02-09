package com.twoday.spaceshipparking;

import com.twoday.spaceshipparking.exceptions.ExistingRecordException;
import com.twoday.spaceshipparking.exceptions.ParkingProhibitedException;
import com.twoday.spaceshipparking.exceptions.ParkingNotFoundException;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;


@RestControllerAdvice
public class SpaceShipParkingGlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ProblemDetail handleInvalidArgument(MethodArgumentNotValidException exception) {
        ProblemDetail problemDetail = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
        problemDetail.setTitle("Method argument not valid");
        exception.getBindingResult().getFieldErrors()
                        .forEach(error -> problemDetail
                                .setDetail(error.getField() + ": " + error.getDefaultMessage()));
        return problemDetail;
    }

    @ExceptionHandler(ParkingNotFoundException.class)
    public ProblemDetail handleParkingNotFoundException(ParkingNotFoundException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ParkingProhibitedException.class)
    public ProblemDetail handleDoubleParkingException(ParkingProhibitedException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, exception.getMessage());
    }

    @ExceptionHandler(ExistingRecordException.class)
    public ProblemDetail handleExistingRecordException(ExistingRecordException exception) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, exception.getMessage());
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ProblemDetail handleConstraintViolationException(ConstraintViolationException e) {
        return ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST, e.getMessage());
    }
}
