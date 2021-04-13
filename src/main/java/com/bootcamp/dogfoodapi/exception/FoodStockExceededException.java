package com.bootcamp.dogfoodapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class FoodStockExceededException extends Exception {

	public FoodStockExceededException(Long id, int quanitityToIncrement) {
		super(String.format("Foods with %s ID to increment informed exceeds the max stock capacity: %s", id,
				quanitityToIncrement));
	}
}
