package com.ecommerce.Controllers.advice;

import com.ecommerce.DTO.ErrorResponse;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.services.StockService.OutOfStock;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.Arrays;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ResponseEntity<ErrorResponse>handleJpaContrain(MethodArgumentNotValidException exception , HttpServletRequest req){

        List<String> messages = exception.getBindingResult().getFieldErrors().stream().map(e -> {
            return e.getField() + ":"+ e.getDefaultMessage();
        }).toList();

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,messages,req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }
    @ExceptionHandler({ConstraintViolationException.class})
    public ResponseEntity<ErrorResponse>handleJpaContrain(ConstraintViolationException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse>handleBadRequests(BadRequestException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }

    @ExceptionHandler({NotFoundException.class})
    public ResponseEntity<ErrorResponse>handleResourcesNotFound(NotFoundException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.NOT_FOUND,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ret);
    }
    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ErrorResponse>handleJpaContrain(ConflictException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.CONFLICT,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ret);
    }
    @ExceptionHandler({OutOfStock.class})
    public ResponseEntity<ErrorResponse>handleJpaContrain(OutOfStock exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.CONFLICT,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ret);
    }
}
