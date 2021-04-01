package com.bootcamp.dogfoodapi.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum FoodType {
	
	PREMIUM("Premium"),
	STANDARD("Standard"),
	SUPER_PREMIUM("Super Premium"),
	NATURALIS("Naturalis");

	private final String description;
}
