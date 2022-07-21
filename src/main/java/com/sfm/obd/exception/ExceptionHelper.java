package com.sfm.obd.exception;

import org.hibernate.exception.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.sfm.obd.dto.ApiResponse;

@ControllerAdvice
public class ExceptionHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(ExceptionHelper.class);
	
	@ExceptionHandler(value = { EntityNotFoundException.class })
    public ResponseEntity<Object> handleEntityNotFoundException(EntityNotFoundException ex) {
    	logger.error("Introuvable",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Introuvable", ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value()),
        		HttpStatus.INTERNAL_SERVER_ERROR
        		);
    }
	
	@ExceptionHandler(value = { AuthenticationException.class })
    public ResponseEntity<Object> handleAuthenticationException(AuthenticationException ex) {
    	logger.error("Erreur d'authentification",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Erreur d'authentification", ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value()),
        		HttpStatus.INTERNAL_SERVER_ERROR
        		);
    }
	
	
    @ExceptionHandler(value = { ConstraintViolationException.class })
    public ResponseEntity<Object> handleConstraintViolationException(ConstraintViolationException ex) {
    	logger.error("Duplication: ",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Duplication", ex.getMessage(),HttpStatus.INTERNAL_SERVER_ERROR.value()),
        		HttpStatus.INTERNAL_SERVER_ERROR);
    }
    
    @ExceptionHandler(value = { NotAuthorizedException.class })
    public ResponseEntity<Object> handleErrorException(NotAuthorizedException ex) {
    	logger.error("Erreur",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Attention", "Vous n'êtes pas autorisés à effectuer cette action !",HttpStatus.UNAUTHORIZED.value()),
        		HttpStatus.UNAUTHORIZED
        		);
    }
    
    @ExceptionHandler(value = { ErrorException.class })
    public ResponseEntity<Object> handleErrorException(ErrorException ex) {
    	logger.error("Erreur",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Erreur", 
        				(ex.getMessage().isEmpty() || ex.getMessage()==null) ? "Oups, une erreur est survenue !":ex.getMessage(),
        				HttpStatus.INTERNAL_SERVER_ERROR.value()),
        		HttpStatus.INTERNAL_SERVER_ERROR
        		);
    }
    
    @ExceptionHandler(value = { Exception.class })
    public ResponseEntity<Object> handleException(Exception ex) {
    	logger.error("Exception: ",ex.getMessage());
    	ex.printStackTrace();
        return new ResponseEntity<Object>(
        		new ApiResponse("Erreur", "Oups, une erreur est survenue !",HttpStatus.INTERNAL_SERVER_ERROR.value()),
        		HttpStatus.INTERNAL_SERVER_ERROR
        		);
    }
}