package com.bootcamp.dogfoodapi.controller;

import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.exception.FoodAlreadyRegisteredException;
import com.bootcamp.dogfoodapi.exception.FoodNotFoundException;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;

import javax.validation.Valid;
import java.util.List;

@Api("Manages dog food")
public interface FoodControllerDocs {

	@ApiOperation(value = "Food creation operation")
	@ApiResponses(value = {
			@ApiResponse(code = 201, message = "Success food creation"),
			@ApiResponse(code = 400, message = "Missing required fields or wrong fields range value.")
	})
	FoodDTO createFood(FoodDTO foodDTO) throws FoodAlreadyRegisteredException;

	@ApiOperation(value = "Returns foods found by a given name")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "Success food found in the system"),
			@ApiResponse(code = 404, message = "Food with given name not found.")
	})
	FoodDTO findByName(@PathVariable String name) throws FoodNotFoundException;

	@ApiOperation(value = "Returns a list of all foods registered in the system")
	@ApiResponses(value = {
			@ApiResponse(code = 200, message = "List of all foods registered in the system")
	})
	List<FoodDTO> listFoods();

	@ApiOperation(value = "Delete a food found by a given valid id")
	@ApiResponses(value = {
			@ApiResponse(code = 204, message = "Success food deleted in the system"),
			@ApiResponse(code = 404, message = "Food with given id not found.")
	})
	void deleteById(@PathVariable Long id) throws FoodNotFoundException;
}
