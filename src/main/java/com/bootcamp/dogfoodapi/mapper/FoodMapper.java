package com.bootcamp.dogfoodapi.mapper;

import com.bootcamp.dogfoodapi.dto.FoodDTO;
import com.bootcamp.dogfoodapi.entity.Food;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface FoodMapper {

	FoodMapper INSTANCE = Mappers.getMapper(FoodMapper.class);

	Food toModel(FoodDTO foodDTO);

	FoodDTO toDTO(Food food);
}
