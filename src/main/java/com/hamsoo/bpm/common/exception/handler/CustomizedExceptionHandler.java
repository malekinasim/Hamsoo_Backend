package com.hamsoo.bpm.common.exception.handler;

import com.hamsoo.bpm.common.exception.*;
import com.hamsoo.bpm.common.utils.ResourceBundleUtils;
import com.hamsoo.bpm.common.utils.security.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.util.Assert;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.server.MethodNotAllowedException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@ControllerAdvice
public class CustomizedExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomizedExceptionHandler.class);

    @Value("${spring.servlet.multipart.max-file-size}")
    private String maxFileSize;

    @Value("${spring.servlet.multipart.max-request-size}")
    private String maxRequestSize;

    public CustomizedExceptionHandler() {
    }

    @ExceptionHandler({MethodNotAllowedException.class})
    public ResponseEntity<?> batchOperationException(MethodNotAllowedException ex, WebRequest webRequest) throws UnknownHostException {
        List<String> errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage()));
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.METHOD_NOT_ALLOWED);
    }

    @ExceptionHandler({BusinessException.class})
    public ResponseEntity<?> businessException(BusinessException ex, WebRequest webRequest) throws UnknownHostException {
        List<String> errors = null;
        if (ex.getArgs() != null && ex.getArgs().length > 0)
            errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage(), ex.getArgs()));
        else errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage()));
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler({AuthenticationProcessingException.class})
    public ResponseEntity<?> oAuth2AuthenticationProcessingException(AuthenticationProcessingException ex, WebRequest webRequest) throws UnknownHostException {
        List<String> errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage()));
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler({ServiceException.class})
    public ResponseEntity<?> serviceException(ServiceException ex, WebRequest webRequest) throws UnknownHostException {
        List<String> errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage()));
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler({ValidationException.class})
    public ResponseEntity<?> validationException(ValidationException ex, WebRequest webRequest) throws UnknownHostException {
        Assert.notNull(ex.getErrors(), "errors is null!");
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getErrors().getFieldErrors()) {
            errors.add(ResourceBundleUtils.getMessage(error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getErrors().getGlobalErrors()) {
            errors.add(ResourceBundleUtils.getMessage(error.getDefaultMessage()));
        }
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @ExceptionHandler({TokenRefreshException.class})
    public ResponseEntity<?> tokenRefreshException(TokenRefreshException ex, WebRequest webRequest) throws UnknownHostException {
        List<String> errors = null;
        if (ex.getArgs() != null && ex.getArgs().length > 0)
            errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage(), ex.getArgs()));
        else errors = Collections.singletonList(ResourceBundleUtils.getMessage(ex.getMessage()));
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler({MaxUploadSizeExceededException.class})
    public ResponseEntity<?> maxUploadSizeExceededException(MaxUploadSizeExceededException ex, WebRequest webRequest) throws UnknownHostException {
        logger.info(ex.getMessage(), ex);
        String message = ResourceBundleUtils.getMessage("error.maxUploadSizeExceeded", maxFileSize, maxRequestSize);
        List<String> errors = Collections.singletonList(message);
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), webRequest.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), InetAddress.getLocalHost().getHostAddress());
        return new ResponseEntity(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({GeneralErrorException.class})
    public ResponseEntity<?> generalErrorException(GeneralErrorException ex, WebRequest webRequest) throws UnknownHostException {
        logger.info("generalErrorException occurred.");
        String message = ResourceBundleUtils.getMessage("error.errorInOperation");
        ErrorDetails errorDetails = new ErrorDetails(Collections.singletonList(message), LocalDateTime.now(),
                webRequest.getDescription(false), SecurityUtils.getUsernameOfCurrentUser().orElse(null),
                InetAddress.getLocalHost().getHostAddress(), ex.getErrorDtoList(),
                ErrorDetails.ErrorCode.GENERAL_ERROR.getCode());
        return new ResponseEntity(errorDetails, HttpStatus.EXPECTATION_FAILED);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = Collections.singletonList(ex.getMessage());
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), request.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), getHostAddress());
        return handleExceptionInternal(ex, errorDetails, headers, HttpStatus.BAD_REQUEST, request);
    }

    /**
     * @param ex This exception is thrown when argument annotated with @Valid failed validation
     */
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
        List<String> errors = new ArrayList<>();
        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            errors.add(ResourceBundleUtils.getMessage(error.getDefaultMessage()));
        }
        for (ObjectError error : ex.getBindingResult().getGlobalErrors()) {
            errors.add(ResourceBundleUtils.getMessage(error.getDefaultMessage()));
        }
        ErrorDetails errorDetails = new ErrorDetails(errors, LocalDateTime.now(), request.getDescription(false),
                SecurityUtils.getUsernameOfCurrentUser().orElse(null), getHostAddress());
        return handleExceptionInternal(ex, errorDetails, headers, HttpStatus.BAD_REQUEST, request);
    }

    private String getHostAddress() {
        String hostAddress = null;
        try {
            hostAddress = InetAddress.getLocalHost().getHostAddress();
        } catch (UnknownHostException e) {
            logger.error(e.getMessage(), e);
        }
        return hostAddress;
    }
}
