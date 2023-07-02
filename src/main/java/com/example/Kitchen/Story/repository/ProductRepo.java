package com.example.Kitchen.Story.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Kitchen.Story.entity.Products;

public interface ProductRepo extends JpaRepository<Products, Long> {

}
