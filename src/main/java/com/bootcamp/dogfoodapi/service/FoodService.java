package com.bootcamp.dogfoodapi.service;

import lombok.AllArgsConstructor;
import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.entity.Food;
import com.bootcamp.dogfoodapi.exception.FoodStockExceededException;
import com.bootcamp.dogfoodapi.exception.FoodAlreadyRegisteredException;
import com.bootcamp.dogfoodapi.exception.FoodNotFoundException;
import com.bootcamp.dogfoodapi.mapper.FoodMapper;
import com.bootcamp.dogfoodapi.repository.FoodRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;


@Service
@AllArgsConstructor(onConstructor = @__(@Autowired))

/**
 *  Usar a anotação @AllArgsConstructor(onContrstuctor = @__(@Autowired))
 *
 *  é o mesmo que criar construtores tais como:
 *
 *  @Autowired
 *  public FoodService(FoodRepository foodRepository) {
 *      this.foodRepository = foodRepository;
 *  }
 * */
public class FoodService {

	private final FoodRepository foodRepository;
	private final FoodMapper foodMapper = FoodMapper.INSTANCE;

	public FoodDTO createFood(FoodDTO foodDTO) throws FoodAlreadyRegisteredException {
		verifyIsAlreadyRegistered(foodDTO.getName());
		Food food = foodMapper.toModel(foodDTO);
		Food saveFood = foodRepository.save(food);
		return foodMapper.toDTO(saveFood);
	}

	public FoodDTO findByName(String name) throws FoodNotFoundException {
		Food foundFood = foodRepository.findByName(name).orElseThrow(()-> new FoodNotFoundException(name));
		return foodMapper.toDTO(foundFood);
	}

	public List<FoodDTO> listAll() {
		return foodRepository.findAll()
				.stream()
				.map(foodMapper::toDTO)
				.collect(Collectors.toList());
	}

	public void deleteById(Long id) throws FoodNotFoundException {
		veifyIfExists(id);
		foodRepository.deleteById(id);
	}

	private Food veifyIfExists(Long id) throws FoodNotFoundException {
		return foodRepository.findById(id).orElseThrow(() -> new FoodNotFoundException(id));
	}
	

	private void verifyIsAlreadyRegistered(String name) throws FoodAlreadyRegisteredException {
		Optional<Food> optSavedFood = foodRepository.findByName(name);
		if(optSavedFood.isPresent()) {
			throw new FoodAlreadyRegisteredException(name);
		}
	}

	public FoodDTO increment(Long id, int quantityToIncrement) throws FoodNotFoundException, FoodStockExceededException {
		Food footToIncrementStock = veifyIfExists(id);
		int quanityAfterIncrement = quantityToIncrement + footToIncrementStock.getQuantity();
			if(quanityAfterIncrement <= footToIncrementStock.getMax()) {
				footToIncrementStock.setQuantity(footToIncrementStock.getQuantity() + quantityToIncrement);
				Food incrementFoodStock = foodRepository.save(footToIncrementStock);
				return foodMapper.toDTO(incrementFoodStock);
			}
			throw new FoodStockExceededException(id, quantityToIncrement);
	}

	public FoodDTO decrement(Long id, int quantityToDecrement) throws  FoodNotFoundException, FoodStockExceededException {
		Food foodToDecrementStock = veifyIfExists(id);
		int foodStockAfterDecremented = foodToDecrementStock.getQuantity() - quantityToDecrement;
		if(foodStockAfterDecremented >= 0){
			foodToDecrementStock .setQuantity(foodStockAfterDecremented);
			Food decrementedFoodStock = foodRepository.save(foodToDecrementStock);
			return  foodMapper.toDTO(decrementedFoodStock);
		}
		throw new FoodStockExceededException(id, quantityToDecrement);
	}
}
