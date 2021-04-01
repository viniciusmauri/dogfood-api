package com.bootcamp.dogfoodapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FoodAlreadyRegisteredException extends  Exception {

	public FoodAlreadyRegisteredException(String foodName) {
		super(String.format("Food with name %s already registered in the system.", foodName));
	}
}
