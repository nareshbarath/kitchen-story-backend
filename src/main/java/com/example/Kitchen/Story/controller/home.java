package com.example.Kitchen.Story.controller;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class home {

	@GetMapping("/")
	public ResponseEntity<Map<String, Object>> homeRoute() {
		System.out.println("API check");
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("message", "This API for Kitchen story application is working!");
		map.put("status", true);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}
}
