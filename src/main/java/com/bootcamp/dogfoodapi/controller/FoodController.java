package com.bootcamp.dogfoodapi.controller;

import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.dto.QuantityDTO;
import com.bootcamp.dogfoodapi.exception.FoodStockExceededException;
import com.bootcamp.dogfoodapi.exception.FoodAlreadyRegisteredException;
import com.bootcamp.dogfoodapi.exception.FoodNotFoundException;
import com.bootcamp.dogfoodapi.service.FoodService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/v1/foods")
@AllArgsConstructor(onConstructor = @__(@Autowired))
public class FoodController implements FoodControllerDocs {

	private final FoodService foodService;

	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public FoodDTO createFood(@RequestBody @Valid FoodDTO foodDTO) throws FoodAlreadyRegisteredException {
		return foodService.createFood(foodDTO);
	}

	@GetMapping("/{name}")
	public FoodDTO findByName(@PathVariable String name) throws FoodNotFoundException {
		return foodService.findByName(name);
	}

	@GetMapping
	public List<FoodDTO> listFoods(){
		return foodService.listAll();
	}

	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void deleteById(@PathVariable Long id) throws FoodNotFoundException {
	    foodService.deleteById(id);
	}

	@PatchMapping("/{id}/increment")
	public FoodDTO increment(@PathVariable Long id, @RequestBody QuantityDTO quantityDTO) throws FoodNotFoundException, FoodStockExceededException {
	    return foodService.increment(id, quantityDTO.getQuantity());
	}

	@PatchMapping("/{id}/decrement")
	public FoodDTO decrement(@PathVariable Long id, @RequestBody @Valid QuantityDTO quantityDTO) throws  FoodNotFoundException, FoodStockExceededException {
	return  foodService.decrement(id, quantityDTO.getQuantity());
	}

}
