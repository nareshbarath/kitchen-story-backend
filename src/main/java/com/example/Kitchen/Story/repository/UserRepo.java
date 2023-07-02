package com.example.Kitchen.Story.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.Kitchen.Story.entity.Users;

public interface UserRepo extends JpaRepository<Users, Long> {

}
