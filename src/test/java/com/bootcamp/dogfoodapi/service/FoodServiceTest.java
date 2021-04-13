package com.bootcamp.dogfoodapi.service;

import com.bootcamp.dogfoodapi.builder.FoodDTOBuilder;
import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.entity.Food;
import com.bootcamp.dogfoodapi.exception.FoodAlreadyRegisteredException;
import com.bootcamp.dogfoodapi.exception.FoodStockExceededException;
import com.bootcamp.dogfoodapi.exception.FoodNotFoundException;
import com.bootcamp.dogfoodapi.mapper.FoodMapper;
import com.bootcamp.dogfoodapi.repository.FoodRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class FoodServiceTest {

	private static final long INVALID_FOOD_ID = 1L;

	@Mock
	private FoodRepository foodRepository;

	private FoodMapper foodMapper = FoodMapper.INSTANCE;

	@InjectMocks
	private FoodService foodService;

	@Test
	void whenFoodInformedThenItShouldBeCreated() throws FoodAlreadyRegisteredException {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedSavedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findByName(expectedFoodDTO.getName())).thenReturn(Optional.empty());
		when(foodRepository.save(expectedSavedFood)).thenReturn(expectedSavedFood);

		FoodDTO createdFoodDTO = foodService.createFood(expectedFoodDTO);

		assertThat(createdFoodDTO.getId(), is(equalTo(expectedFoodDTO.getId())));
		assertThat(createdFoodDTO.getName(), is(equalTo(expectedFoodDTO.getName())));
		assertThat(createdFoodDTO.getQuantity(), is(equalTo(expectedFoodDTO.getQuantity())));
	}

	@Test
	void whenAlreadyRegisteredFoodInformedThenAnExceptionShouldBeThrown() {
		FoodDTO exipectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food duplicateFood = foodMapper.toModel(exipectedFoodDTO);

		when(foodRepository.findByName(exipectedFoodDTO.getName())).thenReturn(Optional.of(duplicateFood));

		assertThrows(FoodAlreadyRegisteredException.class, () -> foodService.createFood(exipectedFoodDTO));
	}

	@Test
	void whenValidFoodNameIsGivenThenReturnAFood() throws FoodNotFoundException {
		FoodDTO expectedFoundFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFoundFood = foodMapper.toModel(expectedFoundFoodDTO);

		when(foodRepository.findByName(expectedFoundFood.getName())).thenReturn(Optional.of(expectedFoundFood));

		FoodDTO foundFoodDTO = foodService.findByName(expectedFoundFoodDTO.getName());

		assertThat(foundFoodDTO, is(equalTo(expectedFoundFoodDTO)));
	}
	@Test
	void whenNotRegisteredFoodNameIsGivenThenThrowAnException() {
		FoodDTO expectedFoundFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		when(foodRepository.findByName(expectedFoundFoodDTO.getName())).thenReturn(Optional.empty());

		assertThrows(FoodNotFoundException.class, () -> foodService.findByName(expectedFoundFoodDTO.getName()));
	}

	@Test
	void whenListFoodIsCalledThenReturnAListOfFoods() {
		FoodDTO expectedFoundFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFoundFood = foodMapper.toModel(expectedFoundFoodDTO);

		when(foodRepository.findAll()).thenReturn(Collections.singletonList(expectedFoundFood));

		List<FoodDTO> foundListFoodsDTO = foodService.listAll();

		assertThat(foundListFoodsDTO, is(not(empty())));
		assertThat(foundListFoodsDTO.get(0), is(equalTo(expectedFoundFoodDTO)));
	}

	@Test
	void whenListFoodIsCalledThenReturnAnEmptListOfFoods() {
		when(foodRepository.findAll()).thenReturn(Collections.EMPTY_LIST);

		List<FoodDTO> foundListFoodsDTO = foodService.listAll();

		assertThat(foundListFoodsDTO, is(empty()));
	}

	@Test
	void whenExclusionIsCalledWithValidIdThenAFoodShouldBeDeleted() throws FoodNotFoundException {
		FoodDTO expectedDeletedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedDeletedFood = foodMapper.toModel(expectedDeletedFoodDTO);

		when(foodRepository.findById(expectedDeletedFoodDTO.getId())).thenReturn(Optional.of(expectedDeletedFood));
		doNothing().when(foodRepository).deleteById(expectedDeletedFoodDTO.getId());

		foodService.deleteById(expectedDeletedFoodDTO.getId());

		verify(foodRepository, times(1)).findById(expectedDeletedFoodDTO.getId());
	}

	@Test
	void whenIncrementIsCalledThenIncrementFoodStock() throws FoodNotFoundException, FoodStockExceededException {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));
		when(foodRepository.save(expectedFood)).thenReturn(expectedFood);

		int quantityToIncrement = 10;
		int expectedQuantityAfterIncrement = expectedFoodDTO.getQuantity() + quantityToIncrement;

		FoodDTO incrementedFoodDTO = foodService.increment(expectedFoodDTO.getId(), quantityToIncrement);

		assertThat(expectedQuantityAfterIncrement, equalTo(incrementedFoodDTO.getQuantity()));
		assertThat(expectedQuantityAfterIncrement, lessThan(expectedFoodDTO.getMax()));
	}

	@Test
	void whenIncrementIsGreaterThanMaxThenThrowException(){
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));

		int quantityToIncrement = 80;
		assertThrows(FoodStockExceededException.class, () -> foodService.increment(expectedFoodDTO.getId(), quantityToIncrement));
	}

	@Test
	void whenIncrementAfterSumIsGreaterThanMaxThenThrowException() {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));

		int quantityToIncrement = 45;
		assertThrows(FoodStockExceededException.class, () -> foodService.increment(expectedFoodDTO.getId(),
				quantityToIncrement));
	}

	@Test
	void whenIncrementIsCalledWithInvalidIdThenThrowException() {
		int quantityToIncrement = 10;

		when(foodRepository.findById(INVALID_FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(FoodNotFoundException.class, () -> foodService.increment(INVALID_FOOD_ID, quantityToIncrement));
	}


	@Test
	void whenDecrementIsCalledThenDecrementFoodStock() throws FoodNotFoundException, FoodStockExceededException {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));
		when(foodRepository.save(expectedFood)).thenReturn(expectedFood);

		int quantityToDecrement = 5;
		int expectedQuantityAfterDecrement = expectedFoodDTO.getQuantity() - quantityToDecrement;
		FoodDTO incrementedFoodDTO = foodService.decrement(expectedFoodDTO.getId(), quantityToDecrement);

		assertThat(incrementedFoodDTO.getQuantity(), is(equalTo(expectedQuantityAfterDecrement)));
		assertThat(expectedQuantityAfterDecrement, is(greaterThan(0)));
	}

	@Test
	void whenDecrementIsCalledToEmptyStockThenEmptyFoodStock() throws FoodNotFoundException, FoodStockExceededException {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));
		when(foodRepository.save(expectedFood)).thenReturn(expectedFood);

		int quantityToDecrement = 10;
		int expectedQuantityAfterDecrement = expectedFoodDTO.getQuantity() - quantityToDecrement;
		FoodDTO incrementedFoodDTO = foodService.decrement(expectedFoodDTO.getId(), quantityToDecrement);

		assertThat(expectedQuantityAfterDecrement, is(equalTo(0)));
		assertThat(expectedQuantityAfterDecrement, is(equalTo(incrementedFoodDTO.getQuantity())));
	}

	@Test
	void whenDecrementIsLowerThanZeroThenThrowException() {
		FoodDTO expectedFoodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		Food expectedFood = foodMapper.toModel(expectedFoodDTO);

		when(foodRepository.findById(expectedFoodDTO.getId())).thenReturn(Optional.of(expectedFood));

		int quantityToDecrement = 80;
		assertThrows(FoodStockExceededException.class, () -> foodService.decrement(expectedFoodDTO.getId(),
				quantityToDecrement));
	}

	@Test
	void whenDecrementIsCalledWithInvalidIdThenThrowException() {
		int quantityToDecrement = 10;

		when(foodRepository.findById(INVALID_FOOD_ID)).thenReturn(Optional.empty());

		assertThrows(FoodNotFoundException.class, () -> foodService.decrement(INVALID_FOOD_ID, quantityToDecrement));
	}
}

