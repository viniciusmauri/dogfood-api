package com.bootcamp.dogfoodapi.controller;

import com.bootcamp.dogfoodapi.builder.FoodDTOBuilder;
import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.dto.QuantityDTO;
import com.bootcamp.dogfoodapi.exception.FoodNotFoundException;
import com.bootcamp.dogfoodapi.service.FoodService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.servlet.view.json.MappingJackson2JsonView;

import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.mock.http.server.reactive.MockServerHttpRequest.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static com.bootcamp.dogfoodapi.utils.JsonConvertionUtils.asJsonString;
import static org.hamcrest.core.Is.is;

import java.util.Collections;

@ExtendWith(MockitoExtension.class)
public class FoodControllerTest {

	private static final String FOOD_API_URL_PATH = "/api/v1/foods";
	private static final long VALID_FOOD_ID = 1L;
	private static final long INVALID_FOOD_ID = 2l;
	private static String FOOD_API_SUBPATH_INCREMENT_URL = "/increment";
	private static String FOOD_API_SUBPATH_DECREMENT_URL = "/decrement";

	private MockMvc mockMvc;

	@Mock
	private FoodService foodService;

	@InjectMocks
	private FoodController foodController;

	@BeforeEach
	void setUp(){
		Object[] controllers;
		mockMvc = MockMvcBuilders.standaloneSetup(foodController)
				.setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
				.setViewResolvers((s, locale) -> new MappingJackson2JsonView())
				.build();
	}

	@Test
	void whenPOSTIsCalledThenAFoodIsCreated() throws  Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		// when
		when(foodService.createFood(foodDTO)).thenReturn(foodDTO);

		// then
		mockMvc.perform(post(FOOD_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(foodDTO)))
				.andExpect(status().isCreated())
				.andExpect(jsonPath("$.name", is(foodDTO.getName())))
				.andExpect(jsonPath("$.brand", is(foodDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(foodDTO.getType().toString())));
	}

	@Test
	void whenPOSTIsCalledWithoutRequiredFieldThenAnErrorIsReturned() throws Exception {
		//given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		foodDTO.setBrand(null);

		//then
		mockMvc.perform(post(FOOD_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(foodDTO)))
				.andExpect(status().isBadRequest());
	}

	@Test
	void whenGETIsCalledWithValidNameThenOkStatusIsReturned() throws Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		//when
		when(foodService.findByName(foodDTO.getName())).thenReturn(foodDTO);

		// then
		mockMvc.perform(MockMvcRequestBuilders.get(FOOD_API_URL_PATH + "/" + foodDTO.getName())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(foodDTO.getName())))
				.andExpect(jsonPath("$.brand", is(foodDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(foodDTO.getType().toString())));
	}

	@Test
	void whenGETIsCalledWithoutRegisteredNameThenNotFoundStatusIsReturned() throws Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		//when
		when(foodService.findByName(foodDTO.getName())).thenThrow(FoodNotFoundException.class);

		// then
		mockMvc.perform(MockMvcRequestBuilders.get(FOOD_API_URL_PATH + "/" + foodDTO.getName())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void whenGETListWithFoodsIsCalledThenOkStatusIsReturned() throws Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		//when
		when(foodService.listAll()).thenReturn(Collections.singletonList(foodDTO));

		// then
		mockMvc.perform(MockMvcRequestBuilders.get(FOOD_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$[0].name", is(foodDTO.getName())))
				.andExpect(jsonPath("$[0].brand", is(foodDTO.getBrand())))
				.andExpect(jsonPath("$[0].type", is(foodDTO.getType().toString())));
	}

	@Test
	void whenGETListWithoutFoodsIsCalledThenOkStatusIsReturned() throws Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		//when
		when(foodService.listAll()).thenReturn(Collections.singletonList(foodDTO));

		// then
		mockMvc.perform(MockMvcRequestBuilders.get(FOOD_API_URL_PATH)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isOk());
	}

	@Test
	void whenDELETEIsCalledWithValidIdThenNoContentStatusIsReturned() throws Exception {
		// given
		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();

		//when
		doNothing().when(foodService).deleteById(foodDTO.getId());

		// then
		mockMvc.perform(MockMvcRequestBuilders.delete(FOOD_API_URL_PATH + "/" + foodDTO.getId())
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNoContent());
	}

	@Test
	void whenDELETEIsCalledWithInvalidIdThenNotFoundStatusIsReturned() throws Exception {
		//when
		doThrow(FoodNotFoundException.class).when(foodService).deleteById(INVALID_FOOD_ID);

		// then
		mockMvc.perform(MockMvcRequestBuilders.delete(FOOD_API_URL_PATH + "/" + INVALID_FOOD_ID)
				.contentType(MediaType.APPLICATION_JSON))
				.andExpect(status().isNotFound());
	}

	@Test
	void whenPATCHIsCalledToIncrementDiscountThenOKstatusIsReturned() throws Exception {
		QuantityDTO quantityDTO = QuantityDTO.builder()
				.quantity(10)
				.build();

		FoodDTO foodDTO = FoodDTOBuilder.builder().build().toFoodDTO();
		foodDTO.setQuantity(foodDTO.getQuantity() + quantityDTO.getQuantity());

		when(foodService.increment(VALID_FOOD_ID, quantityDTO.getQuantity())).thenReturn(foodDTO);

		mockMvc.perform(MockMvcRequestBuilders.patch(FOOD_API_URL_PATH + "/" + VALID_FOOD_ID + FOOD_API_SUBPATH_INCREMENT_URL)
				.contentType(MediaType.APPLICATION_JSON)
				.content(asJsonString(quantityDTO))).andExpect(status().isOk())
				.andExpect(jsonPath("$.name", is(foodDTO.getName())))
				.andExpect(jsonPath("$.brand", is(foodDTO.getBrand())))
				.andExpect(jsonPath("$.type", is(foodDTO.getType().toString())))
				.andExpect(jsonPath("$.quantity", is(foodDTO.getQuantity())));
	}

}
