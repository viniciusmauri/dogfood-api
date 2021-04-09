package com.bootcamp.dogfoodapi.builder;

import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.enums.FoodType;
import lombok.Builder;

@Builder
public class FoodDTOBuilder {
	@Builder.Default
	private Long id = 1L;

	@Builder.Default
	private String name = "Magnus PREMIUM";

	@Builder.Default
	private String brand = "Mars";

	@Builder.Default
	private Integer max = 50;

	@Builder.Default
	private Integer quantity = 10;

	@Builder.Default
	private FoodType type = FoodType.PREMIUM;

	public FoodDTO toFoodDTO(){
		return new FoodDTO(id,
				name,
				brand,
				max,
				quantity,
				type);
	}
}