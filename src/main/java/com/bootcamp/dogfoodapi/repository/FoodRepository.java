package com.bootcamp.dogfoodapi.repository;

import com.bootcamp.dogfoodapi.entity.Food;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface FoodRepository extends JpaRepository<Food, Long> {

	Optional<Food> findByName(String name);
}
