package com.hussain.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class FoodOrderGlobalException {
	
	@ExceptionHandler(VendorNameIsNotFount.class)
	public ResponseEntity<String>  vendorNameIsNotFoundException(VendorNameIsNotFount  ex) {
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
	
	@ExceptionHandler(FoodNotAvailable.class)
	public ResponseEntity<String> foodNameIsNotAvailbe(FoodNotAvailable ex) {
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
    
	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<String> userNotFoundException(UserNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(), HttpStatus.UNAUTHORIZED);
	}
	
	@ExceptionHandler(OrderNotFoundException.class)
	public ResponseEntity<String> orderNotFoundException(OrderNotFoundException ex) {
		return new ResponseEntity<>(ex.getMessage(),HttpStatus.NOT_FOUND);
	}
}
