package com.ecommerce;

import com.ecommerce.util.ErrorResponse;
import com.ecommerce.Exception.BadRequestException;
import com.ecommerce.Exception.ConflictException;
import com.ecommerce.Exception.NotFoundException;
import com.ecommerce.Stock.exceptions.OutOfStock;
import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MultipartException;

import java.time.Instant;
import java.util.List;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler({MethodArgumentNotValidException.class,})
    public ResponseEntity<ErrorResponse>handleConstrain(MethodArgumentNotValidException exception , HttpServletRequest req){

        List<String> messages = exception.getBindingResult().getFieldErrors().stream().map(e -> e.getField() + ":"+ e.getDefaultMessage()).toList();

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,messages,req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }
    @ExceptionHandler({ValidationException.class, MultipartException.class})
    public ResponseEntity<ErrorResponse> handleConstrain(ValidationException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }
    @ExceptionHandler({BadRequestException.class})
    public ResponseEntity<ErrorResponse>handleBadRequests(BadRequestException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.BAD_REQUEST,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.badRequest().body(ret);
    }

    @ExceptionHandler({NotFoundException.class, EntityNotFoundException.class})
    public ResponseEntity<ErrorResponse>handleResourcesNotFound(Exception exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.NOT_FOUND,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ret);
    }
    @ExceptionHandler({ConflictException.class})
    public ResponseEntity<ErrorResponse>handleConflit(ConflictException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.CONFLICT,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ret);
    }
    @ExceptionHandler({OutOfStock.class})
    public ResponseEntity<ErrorResponse>handleoutOfStock(OutOfStock exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.CONFLICT,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(ret);
    }

    @ExceptionHandler({BadCredentialsException.class})
    public ResponseEntity<ErrorResponse>handleoutOfStock(BadRequestException exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.UNAUTHORIZED,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ret);
    }
    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorResponse>handleUnExpectedException(Exception exception , HttpServletRequest req){

        ErrorResponse ret = new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR,List.of(exception.getMessage()),req.getRequestURI(), Instant.now());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ret);
    }

}
