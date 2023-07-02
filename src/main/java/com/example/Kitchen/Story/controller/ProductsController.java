package com.example.Kitchen.Story.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import com.example.Kitchen.Story.Security.Security;
import com.example.Kitchen.Story.entity.*;
import com.example.Kitchen.Story.repository.ProductRepo;
import com.example.Kitchen.Story.repository.UserRepo;

@RestController
public class ProductsController {

	@Autowired
	ProductRepo pr;

	@Autowired
	UserRepo ur;

	@PostMapping("products/add")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> addProduct(@RequestBody Map<String, Object> req,
			@RequestHeader("token") String token) {
		Map<String, Object> map = new HashMap<String, Object>();

		Security s = new Security(ur);
		Users user = s.decodeToken(token);
		if (!user.getRole().equals("Admin")) {
			map.put("status", false);
			map.put("message", "Unauthorized");
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.UNAUTHORIZED);
		}

		String name = req.get("name").toString();
		String price = req.get("price").toString();
		String description = req.get("description").toString();
		String image = req.get("image").toString();

		Products newproduct = new Products(name, Long.parseLong(price), description, image);
		Products newProduct = pr.save(newproduct);
		map.put("message", "Product added successfully");
		map.put("product_ID", newProduct.getId());
		map.put("status", true);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@GetMapping("products/list")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> listProducts() {
		Map<String, Object> map = new HashMap<String, Object>();

		List<Products> products = pr.findAll();
		map.put("status", true);
		map.put("message", "Data found");
		map.put("data", products);
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

	@PostMapping("products/delete")
	@CrossOrigin(origins = "http://localhost:4200/")
	public ResponseEntity<Map<String, Object>> deleteProduct(@RequestBody Map<String, Object> req,
			@RequestHeader("token") String token) {
		Map<String, Object> map = new HashMap<String, Object>();

		Security s = new Security(ur);
		Users user = s.decodeToken(token);
		if (!user.getRole().equals("Admin")) {
			map.put("status", false);
			map.put("message", "Unauthorized");
			return new ResponseEntity<Map<String, Object>>(map, HttpStatus.UNAUTHORIZED);
		}

		Integer id = (Integer) req.get("id");
		Products p = new Products();
		p.setId(id);
		Example example = Example.of(p);
		Optional findProduct = pr.findOne(example);
		Products product = (Products) findProduct.get();
		pr.delete(product);

		map.put("status", true);
		map.put("message", "Product deleted successfuly");
		return new ResponseEntity<Map<String, Object>>(map, HttpStatus.OK);
	}

}
