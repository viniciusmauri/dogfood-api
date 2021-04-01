package com.bootcamp.dogfoodapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class FoodNotFoundException extends Exception {

	public FoodNotFoundException(String formName) {
		super(String.format("Food with name %s not found in the system.", formName));
	}

	public FoodNotFoundException(Long id){
		super(String.format("Food with id %s not found in the system.", id));
	}
}
