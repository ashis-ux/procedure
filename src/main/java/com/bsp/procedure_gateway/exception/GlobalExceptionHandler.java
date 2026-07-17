package com.bsp.procedure_gateway.exception;

 
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.bsp.procedure_gateway.dto.ErrorResponse;
import com.bsp.procedure_gateway.dto.ErrorResponse1;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import java.time.LocalDateTime;
import java.util.UUID;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ProcedureGatewayException.class)
    public ResponseEntity<ErrorResponse> handleProcedureException(
            ProcedureGatewayException exception) {

        ErrorResponse response = ErrorResponse.builder()
                .requestId(UUID.randomUUID().toString())
                .errorCode(exception.getErrorCode())
                .errorMessage(exception.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

//    @ExceptionHandler(Exception.class)
//    public ResponseEntity<ErrorResponse> handleException(
//            Exception exception) {
//
//        ErrorResponse response = ErrorResponse.builder()
//                .requestId(UUID.randomUUID().toString())
//                .errorCode("SYS_500")
//                .errorMessage("Internal server error")
//                .timestamp(LocalDateTime.now())
//                .build();
//
//        return ResponseEntity
//                .status(HttpStatus.INTERNAL_SERVER_ERROR)
//                .body(response);
//    }
    
    @ExceptionHandler(InvalidTokenException.class)
    public ResponseEntity<ErrorResponse> handleInvalidTokenException(
            InvalidTokenException ex) {

        ErrorResponse response = ErrorResponse.builder()
                .requestId(UUID.randomUUID().toString())
                .errorCode("TOKEN_401")
                .errorMessage(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(response);
    }
    
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ErrorResponse1> handleBadRequestException(
            BadRequestException ex,
            HttpServletRequest request) {

        log.error("Bad Request : {}", ex.getMessage());

        ErrorResponse1 response = ErrorResponse1.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.BAD_REQUEST.value())
                .error(HttpStatus.BAD_REQUEST.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(response);
    }

    /**
     * Resource Already Exists
     */
    @ExceptionHandler(ResourceAlreadyExistsException.class)
    public ResponseEntity<ErrorResponse1> handleResourceAlreadyExistsException(
            ResourceAlreadyExistsException ex,
            HttpServletRequest request) {

        log.error("Resource Already Exists : {}", ex.getMessage());

        ErrorResponse1 response = ErrorResponse1.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse1> handleResourceNotFoundException(
    		ResourceNotFoundException ex,
            HttpServletRequest request) {

        log.error("Resource Already Exists : {}", ex.getMessage());

        ErrorResponse1 response = ErrorResponse1.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }
    
    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ErrorResponse1> handleValidationException(
    		ValidationException ex,
            HttpServletRequest request) {

        log.error("Resource Already Exists : {}", ex.getMessage());

        ErrorResponse1 response = ErrorResponse1.builder()
                .timestamp(LocalDateTime.now())
                .status(HttpStatus.CONFLICT.value())
                .error(HttpStatus.CONFLICT.getReasonPhrase())
                .message(ex.getMessage())
                .path(request.getRequestURI())
                .build();

        return ResponseEntity
                .status(HttpStatus.CONFLICT)
                .body(response);
    }

}